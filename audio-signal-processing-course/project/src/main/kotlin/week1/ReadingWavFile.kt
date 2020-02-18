package week1

import io.wavebeans.execution.LocalOverseer
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.io.wave
import io.wavebeans.lib.stream.rangeProjection
import io.wavebeans.lib.stream.trim
import java.io.File
import java.util.concurrent.TimeUnit

fun main() {
    val inputFile = "piano.wav"
    val outputFile = "piano.csv"

    val output = wave("file://${File(inputFile).absolutePath}")
            .rangeProjection(1133, timeUnit = TimeUnit.MILLISECONDS)
            .trim(226, TimeUnit.MICROSECONDS)
            .toCsv("file://${File(outputFile).absolutePath}")

    LocalOverseer(listOf(output)).use {
        if (!it.eval(44100.0f).all { it.get() }) {
            println("Problems during launching the stream")
        }
    }
}