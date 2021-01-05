package wave.blog.samplerecorder.client

import io.wavebeans.execution.SingleThreadedOverseer
import io.wavebeans.lib.*
import io.wavebeans.lib.io.*
import io.wavebeans.lib.stream.*
import io.wavebeans.lib.stream.window.window
import javax.sound.sampled.*

fun main() {
    println("\nMixers:\n" + AudioSystem.getMixerInfo().joinToString("\n") {
        val mixer = AudioSystem.getMixer(it)
        "${it.name} (${it.vendor}) v.${it.version}: ${it.description}\n" +
                "\tSources: ${mixer.sourceLineInfo.contentToString()}\n" +
                "\tTargets: ${mixer.targetLineInfo.contentToString()}\n"
    })

    val captureSampleRate = 16000.0f
    val outputSampleRate = 44100.0f
    val captureLineFn = CaptureLineFn(captureSampleRate, BitDepth.BIT_16, "Default Audio Device")
    val silenceSize = (outputSampleRate * 2).toInt() // 2 sec
    val silence = input { sampleVectorOf(1024) { _, _ -> ZeroSample } }
    val o =
//        (
//            input { sampleVectorOf(1.0) }.trim(1000)..input(captureSampleRate, captureLineFn)
//        )
        (440.sine().window(64).map { sampleVectorOf(it) }.trim(1000) ..
                silence.trim(2500) ..
                220.sine().window(64).map { sampleVectorOf(it) }.trim(1500) ..
                silence.trim(10000)
                )
            .flatten()
            .resample()
            .detectSilence(silenceSize, 0.05)
            .toMono16bitWav("file:///users/asubb/tmp/wav/record.wav") {
                it?.let { String.format("%.5f", it) } ?: "0"
            }

    val overseer = SingleThreadedOverseer(listOf(o))
    Runtime.getRuntime().addShutdownHook(Thread {
        overseer.close()
        captureLineFn.close()
    })

    println("evaluation starting...")
    overseer.eval(outputSampleRate).all { it.get().finished }
    println("evaluation finished.")
}

