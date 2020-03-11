package week2

import evaluate
import io.wavebeans.lib.io.input
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.stream.trim
import java.io.File
import kotlin.math.PI
import kotlin.math.cos

fun main() {
    val outputFile = "sine.csv"
    val fs = 50.0f

    input { (idx, sampleRate) ->
        val f = 10.0f
        val a = 1.0
        val phi = 1.0
        val x = idx / sampleRate
        a * cos(x * 2.0 * PI * f + phi)
    }
            .trim(100)
            .toCsv("file:///${File(outputFile).absolutePath}")
            .evaluate(sampleRate = fs)
}