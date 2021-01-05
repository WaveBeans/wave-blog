package wave.blog.samplerecorder.client

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.*
import io.wavebeans.lib.*
import io.wavebeans.lib.io.*
import io.wavebeans.lib.stream.*
import io.wavebeans.lib.stream.window.window
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.sign

class SilenceDetectorSpec : Spek({
    describe("Out of made up short signal") {
        it("should detect signal at the beginning") {
            val output = streamOf(0.1, 0.2, 0.3, -0.1, -0.2, -0.3, -0.4, 0.03, 0.03, 0.02, -0.05, 0.0)
                .detectSilence(5, 0.4, attackThreshold = 0.05)
                .toList(1000.0f, 7)
                .also(::printSampleVector)
            assertThat(output).all {
                index(0).all {
                    payload().isEqualTo(sampleVectorOf(0.1, 0.2))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(1).all {
                    payload().isEqualTo(sampleVectorOf(0.3, -0.1))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(2).all {
                    payload().isEqualTo(sampleVectorOf(-0.2, -0.3))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(3).all {
                    payload().isEqualTo(sampleVectorOf(-0.4, 0.03))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(4).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
                index(5).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
                index(6).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
            }
        }
        it("should detect signal at the end") {
            val output = streamOf(
                0.01, -0.01,
                0.01, -0.01,
                0.0, 0.1,
                0.2, 0.3,
                -0.1, -0.2,
                -0.3, -0.4
            )
                .detectSilence(5, 0.4, attackThreshold = 0.05)
                .toList(1000.0f, 7)
                .also(::printSampleVector)
            assertThat(output).all {
                index(0).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
                index(1).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
                index(2).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.1))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(3).all {
                    payload().isEqualTo(sampleVectorOf(0.2, 0.3))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(4).all {
                    payload().isEqualTo(sampleVectorOf(-0.1, -0.2))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(5).all {
                    payload().isEqualTo(sampleVectorOf(-0.3, -0.4))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(6).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
            }
        }

        it("should detect a few consecutive samples") {
            val output = streamOf(
                0.5, 0.5, 0.5, 0.5, 0.5,      // sample1
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, // silence
                0.5, 0.5, 0.5, 0.5, 0.5,      // sample2
                0.0, 0.0, 0.0, 0.0, 0.0,      // silence
            )
                .detectSilence(5, 0.4, attackThreshold = 0.05)
                .toList(1000.0f, 10)
                .also(::printSampleVector)
            assertThat(output).all {
                index(0).all {
                    payload().isEqualTo(sampleVectorOf(0.5, 0.5))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(1).all {
                    payload().isEqualTo(sampleVectorOf(0.5, 0.5))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(2).all {
                    payload().isEqualTo(sampleVectorOf(0.5, 0.0))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(3).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
                index(4).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
                index(5).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.5))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(6).all {
                    payload().isEqualTo(sampleVectorOf(0.5, 0.5))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(7).all {
                    payload().isEqualTo(sampleVectorOf(0.5, 0.5))
                    signal().isEqualTo(OpenGateOutputSignal)
                }
                index(8).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
                index(9).all {
                    payload().isEqualTo(sampleVectorOf(0.0, 0.0))
                    signal().isEqualTo(CloseGateOutputSignal)
                }
            }
        }
    }

    describe("Real signal") {
        it("should cut out samples") {
            val sampleRate = 100.0f
            val silenceSize = 15
            val output = (40.sine() * 1.sine()).map { if (it.absoluteValue > 0.5) it else 0.01 * sign(it) }
                .detectSilence(silenceSize, 0.2, attackThreshold = 0.03, noiseThreshold = 0.02)
                .toList(sampleRate, 40)
                .also(::printSampleVector)

            assertThat(output.flatMap { it.payload.toList() }).all {
                timeRange(0.00, 0.17, sampleRate).each { it.isNotEqualTo(0.0) }
                timeRange(0.18, 0.33, sampleRate).each { it.isEqualTo(0.0) }
                timeRange(0.36, 0.63, sampleRate).each { it.isNotEqualTo(0.0) }
                timeRange(0.66, 0.81, sampleRate).each { it.isEqualTo(0.0) }
                timeRange(0.84, 1.00, sampleRate).each { it.isNotEqualTo(0.0) }
            }
        }
        it("should not cut out samples") {
            val sampleRate = 100.0f
            val silenceSize = 30
            val output = (40.sine() * 1.sine()).map { if (it.absoluteValue > 0.5) it else 0.01 * sign(it) }
                .detectSilence(silenceSize, 0.2, attackThreshold = 0.03, noiseThreshold = 0.02)
                .toList(sampleRate, 20)
                .also(::printSampleVector)

            assertThat(output.flatMap { it.payload.toList() }).all {
                timeRange(0.00, 1.00, sampleRate).each { it.isNotEqualTo(0.0) }
            }
        }
    }

    describe("Marking signal") {
        val attackThreshold = 0.05
        val noiseThreshold = 0.05
        it("should mark the good signal at the beginning") {
            val output = streamOf(
                0.1, 0.2,
                0.3, -0.1,
                -0.2, -0.3,
                -0.4, 0.03,
                0.03, 0.02,
                -0.05, 0.0
            )
                .window(5, 2)
                .merge(input { it }) { (window, indexAndSampleRate) ->
                    requireNotNull(window)
                    requireNotNull(indexAndSampleRate)
                    window to indexAndSampleRate
                }
                .map(SignalMarkerFn(attackThreshold, noiseThreshold))
                .toList(1000.0f, 8)
                .also(::printSampleVector)
            assertThat(output).all {
                index(0).signal().isEqualTo(OpenGateOutputSignal)
                index(1).signal().isEqualTo(OpenGateOutputSignal)
                index(2).signal().isEqualTo(OpenGateOutputSignal)
                index(3).signal().isEqualTo(OpenGateOutputSignal)
                index(4).signal().isEqualTo(CloseGateOutputSignal)
                index(5).signal().isEqualTo(CloseGateOutputSignal)
                index(6).signal().isEqualTo(CloseGateOutputSignal)
                index(7).signal().isEqualTo(CloseGateOutputSignal)
            }
        }
        it("should mark the good signal at the end") {
            val output = streamOf(
                -0.02, 0.02,
                0.03, 0.02,
                0.0, -0.4,
                -0.3, -0.2,
                -0.1, 0.1,
                0.2, 0.3,
            )
                .window(5, 2)
                .merge(input { it }) { (window, indexAndSampleRate) ->
                    requireNotNull(window)
                    requireNotNull(indexAndSampleRate)
                    window to indexAndSampleRate
                }
                .map(SignalMarkerFn(attackThreshold, noiseThreshold))
                .toList(1000.0f, 8)
                .also(::printSampleVector)
            assertThat(output).all {
                index(0).signal().isEqualTo(CloseGateOutputSignal)
                index(1).signal().isEqualTo(OpenGateOutputSignal)
                index(2).signal().isEqualTo(OpenGateOutputSignal)
                index(3).signal().isEqualTo(OpenGateOutputSignal)
                index(4).signal().isEqualTo(OpenGateOutputSignal)
                index(5).signal().isEqualTo(OpenGateOutputSignal)
                index(6).signal().isEqualTo(CloseGateOutputSignal)
                index(7).signal().isEqualTo(CloseGateOutputSignal)
            }
        }
    }
})


private fun printSampleVector(output: List<ManagedSampleVector>) {
    println(output.joinToString("\n") {
        "${it.payload.contentToString()} [${it.signal} ${it.argument}]"
    })
}

private fun Assert<List<Double>>.timeRange(from: Double, to: Double, sampleRate: Float) =
    prop("${from}s-${to}s") { it.subList(floor(from * sampleRate).toInt(), floor(to * sampleRate).toInt()) }

private fun <T : Any> Assert<Managed<OutputSignal, Float, T>>.payload() = prop("payload") { it.payload }

private fun <T : Any> Assert<Managed<OutputSignal, Float, T>>.signal() = prop("signal") { it.signal }

private fun streamOf(vararg doubles: Double) = doubles.map { sampleOf(it) }.input().stream(AfterFilling(ZeroSample))

private fun <T : Any> BeanStream<T>.toList(sampleRate: Float, take: Int = Int.MAX_VALUE) =
    this.asSequence(sampleRate).take(take).toList()
