package week1

import evaluate
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.io.wave
import io.wavebeans.lib.stream.rangeProjection
import io.wavebeans.lib.stream.trim
import java.io.File
import java.util.concurrent.TimeUnit

fun main() {
    val inputFile = "audio-signal-processing-course/project/piano.wav"
    val outputFile = "piano.csv"

    wave("file://${File(inputFile).absolutePath}")
            .rangeProjection(1133, timeUnit = TimeUnit.MILLISECONDS)
            .trim(226, TimeUnit.MICROSECONDS)
            .toCsv("file://${File(outputFile).absolutePath}")
            .evaluate()
}