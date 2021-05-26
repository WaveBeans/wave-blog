package week1

import io.wavebeans.lib.io.wave
import io.wavebeans.lib.stream.trim
import java.io.File

fun main() {
    val inputFile = "audio-signal-processing-course/project/piano.wav"

    val M = 2
    val hopSamples = wave("file://${File(inputFile).absolutePath}")
            .trim(1)
            .asSequence(44100.0f)
            .windowed(size = M, step = M) { it.last() }
            .toList()
    println(hopSamples)
}