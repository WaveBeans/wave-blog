package wave.blog.samplerecorder.client

import io.wavebeans.execution.SingleThreadedOverseer
import io.wavebeans.lib.BitDepth
import io.wavebeans.lib.io.input
import io.wavebeans.lib.io.toMono16bitWav
import io.wavebeans.lib.sampleVectorOf
import io.wavebeans.lib.stream.flatten
import io.wavebeans.lib.stream.rangeTo
import io.wavebeans.lib.stream.resample
import io.wavebeans.lib.stream.trim
import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.sound.sampled.AudioSystem

object Const {
    val start: Long = System.currentTimeMillis() / 1000
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
}

fun main() {
    println("\nMixers:\n" + AudioSystem.getMixerInfo()
        .mapNotNull {
            val mixer = AudioSystem.getMixer(it)

            if (mixer.targetLineInfo.isNotEmpty()) {
                "${it.name} (${it.vendor}) v.${it.version}: ${it.description}\n" +
                        mixer.targetLineInfo.map { line -> mixer.getLine(line) }.joinToString("\n") { line ->
                            "\t\t${line.lineInfo}\n"
                        }
            } else {
                null
            }

        }
        .joinToString("\n")
    )

    val captureSampleRate = 44100.0f
    val outputSampleRate = 44100.0f
    val bitDepth = BitDepth.BIT_16
    val deviceName = "iRig HD 2"
    val silenceLength = 1.5
    val dir = "/users/asubb/tmp/my_recordings/"
    val outputPath = "file://${dir}record.wav"

    File(dir).mkdirs()

    val silenceSize = (outputSampleRate * silenceLength).toInt() // 2 sec
    val o =
        (input { sampleVectorOf(1.0) }.trim(1000).. // no close gate at the beginning, so that's a workaround to close it up
                input(captureSampleRate, CaptureLineFn(captureSampleRate, bitDepth, deviceName)))
            .flatten()
            .resample()
            .detectSilence(silenceSize, 0.1, attackThreshold = 0.0001, noiseThreshold = 0.0005)
            .toMono16bitWav(outputPath) {
                Const.formatter.format(
                    ZonedDateTime.ofInstant(
                        Instant.ofEpochSecond(
                            Const.start + (it?.toLong() ?: 0)
                        ),
                        ZoneOffset.systemDefault()
                    )
                )
            }

    val overseer = SingleThreadedOverseer(listOf(o))

    Runtime.getRuntime().addShutdownHook(Thread {
        overseer.close()
        CaptureLineFn(captureSampleRate, bitDepth, deviceName).close()
    })

    println("evaluation starting...")
    overseer.eval(outputSampleRate).all { it.get().finished }
    println("evaluation finished.")
}

