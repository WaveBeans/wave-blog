package wave.blog.samplerecorder.client

import io.wavebeans.lib.*
import io.wavebeans.lib.io.*
import io.wavebeans.lib.stream.flatMap
import io.wavebeans.lib.stream.map
import io.wavebeans.lib.stream.merge
import io.wavebeans.lib.stream.window.Window
import io.wavebeans.lib.stream.window.window
import mu.KotlinLogging
import kotlin.math.absoluteValue

typealias ManagedWindowSample = Managed<OutputSignal, Float, Window<Sample>>
typealias ManagedSampleVector = Managed<OutputSignal, Float, SampleVector>
typealias WindowIndexSampleRate = Pair<Window<Sample>, Pair<Long, Float>>

fun BeanStream<Sample>.detectSilence(
    silenceSize: Int,
    sensitivity: Double,
    attackThreshold: Double = 0.1,
    noiseThreshold: Double = 0.05
): BeanStream<ManagedSampleVector> {
    require(sensitivity > 0.0 && sensitivity <= 1.0) { sensitivity > 0.0 && sensitivity <= 1.0 }
    return this.window(silenceSize, (silenceSize * sensitivity).toInt())
        .merge(input { it }) { (window, indexAndSampleRate) ->
            requireNotNull(window)
            requireNotNull(indexAndSampleRate)
            window to indexAndSampleRate
        }
        .map(SignalMarkerFn(attackThreshold, noiseThreshold))
        .flatMap(ChunkingFn())
        .flatMap(NoiseGatingFn(noiseThreshold))
}

internal class SignalMarkerFn(initParameters: FnInitParameters) :
    Fn<WindowIndexSampleRate, ManagedWindowSample>(initParameters) {

    constructor(attackThreshold: Double, noiseThreshold: Double) : this(
        FnInitParameters()
            .also {
                require(attackThreshold > 0.0 && attackThreshold <= 1.0) { "attackThreshold > 0.0 && attackThreshold <= 1.0" }
                require(noiseThreshold >= 0.0 && noiseThreshold < 1.0) { "noiseThreshold >= 0.0 && noiseThreshold < 1.0" }
            }
            .add("attackThreshold", attackThreshold)
            .add("noiseThreshold", noiseThreshold)
    )

    private val attackThreshold by lazy { initParameters.double("attackThreshold") }
    private val noiseThreshold by lazy { initParameters.double("noiseThreshold") }

    private var log = KotlinLogging.logger { }

    override fun apply(argument: WindowIndexSampleRate): ManagedWindowSample {
        val (window, indexAndSampleRate) = argument
        val (index, fs) = indexAndSampleRate
        val timeOffsetSec = (window.step * index) / fs

        val x = window.elements
        val y = x.map { if (it.absoluteValue > noiseThreshold) it.absoluteValue else ZeroSample }

        val signalAttack = y.average()
        log.debug { "The cut off signal attack is $signalAttack (threshold=$attackThreshold), the signal is $y" }
        return if (signalAttack < attackThreshold) {
            window.withOutputSignal(CloseGateOutputSignal, timeOffsetSec)
        } else {
            window.withOutputSignal(OpenGateOutputSignal, timeOffsetSec)
        }
    }
}

internal class NoiseGatingFn(initParameters: FnInitParameters) :
    Fn<ManagedSampleVector, Iterable<ManagedSampleVector>>(initParameters) {

    constructor(noiseThreshold: Double) : this(
        FnInitParameters()
            .add("noiseThreshold", noiseThreshold)
    )

    private val noiseThreshold by lazy { initParams.double("noiseThreshold") }

    @Volatile
    private var isGating = false

    override fun apply(argument: ManagedSampleVector): Iterable<ManagedSampleVector> {
        return listOf(
            if (argument.signal == OpenGateOutputSignal) {
                if (isGating && argument.payload.all { it < noiseThreshold }) {
                    sampleVectorOf(argument.payload.size) { _, _ -> ZeroSample }
                        .withOutputSignal(CloseGateOutputSignal, argument.argument)
                } else {
                    isGating = false
                    argument
                }
            } else {
                isGating = true
                argument
            }
        )
    }

}

internal class ChunkingFn : Fn<ManagedWindowSample, Iterable<ManagedSampleVector>>() {

    private val log = KotlinLogging.logger { }

    @Volatile
    private var overlap: Int = 0

    @Volatile
    private var kickedIn: Boolean = false

    override fun apply(argument: ManagedWindowSample): List<ManagedSampleVector> {
        log.debug {
            "Overlap=${overlap} Signal=${argument.signal}, Argument=${argument.argument}, " +
                    "Payload=${argument.payload.elements}"
        }
        val window = argument.payload
        return listOf(
            when {
                argument.signal == OpenGateOutputSignal && overlap == 0 -> {
                    log.debug { "It is a good signal samples" }
                    kickedIn = true
                    sampleVectorOf(window.step) { i, _ -> window.elements[i] }
                }
                !kickedIn && argument.signal == CloseGateOutputSignal -> {
                    log.debug { "The signal is not kicked in but silence detected. Skipping" }
                    sampleVectorOf(window.step) { _, _ -> ZeroSample }
                }
                kickedIn && argument.signal == CloseGateOutputSignal && overlap == 0 -> {
                    overlap = window.size - window.step
                    log.debug { "Starting the silence sequence. The calculated overlap is $overlap" }
                    sampleVectorOf(window.step) { _, _ -> ZeroSample }
                }
                kickedIn && /*argument.signal == CloseGateOutputSignal &&*/ overlap <= window.step -> {
                    log.debug { "Ending the silence sequence. The overlap is $overlap < step ${window.step}" }
                    sampleVectorOf(window.step) { i, _ -> if (i < overlap) ZeroSample else window.elements[i] }
                        .also { overlap = 0 }
                }
                kickedIn && /*argument.signal == CloseGateOutputSignal && */overlap > window.step -> {
                    overlap -= window.step
                    log.debug { "Continuing the silence sequence. The remaining overlap is $overlap" }
                    sampleVectorOf(window.step) { _, _ -> ZeroSample }
                }
                else -> throw UnsupportedOperationException(
                    "signal=${argument.signal}, kickedIn=$kickedIn, " +
                            "overlap=$overlap, window.step=${window.step}"
                )
            }.withOutputSignal(argument.signal, argument.argument)
                .also { log.debug { "Returning payload=${it.payload.contentToString()}" } }
        )
    }
}