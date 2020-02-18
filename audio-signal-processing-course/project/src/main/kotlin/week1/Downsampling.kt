package week1

import evaluate
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.io.wave
import io.wavebeans.lib.stream.map
import io.wavebeans.lib.stream.window.window
import java.io.File

fun main() {
    val inputFile = "piano.wav"
    val outputFile = "piano-downsampled.csv"

    val downsamplingFactor = 2
    wave("file://${File(inputFile).absolutePath}")
            .window(downsamplingFactor)
            .map { it.elements.last() }
            .toCsv("file://${File(outputFile).absolutePath}")
            .evaluate()
}
