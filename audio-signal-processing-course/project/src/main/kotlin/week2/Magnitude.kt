package week2

import evaluate
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.math.ComplexNumber
import io.wavebeans.lib.stream.SampleCountMeasurement
import io.wavebeans.lib.stream.map
import io.wavebeans.lib.stream.rangeProjection
import java.io.File

fun main() {
    val outputFile = "magnitude.csv"

    val x = listOf(1, 2, 3, 4)
    val n = 4

    SampleCountMeasurement.registerType(List::class) { it.size }
    dft(n, x)
            .rangeProjection(0, x.size * 1000L)
            .map { it.map(ComplexNumber::abs) }
            .toCsv(
                    uri = "file://${File(outputFile).absolutePath}",
                    header = listOf("idx", "Magnitude values"),
                    elementSerializer = { (idx, _, value) -> listOf(idx.toString(), value.toString()) }
            )
            .evaluate(1.0f)
}