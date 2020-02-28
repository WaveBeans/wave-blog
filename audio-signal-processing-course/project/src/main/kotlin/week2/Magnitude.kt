package week2

import evaluate
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.math.ComplexNumber
import io.wavebeans.lib.stream.map
import java.io.File

fun main() {
    val outputFile = "magnitude.csv"

    val x = listOf(1, 2, 3, 4)
    val n = x.size
    dft(n, x)
            .map { it.map(ComplexNumber::abs) }
            .toCsv(
                    uri = "file://${File(outputFile).absolutePath}",
                    header = listOf("idx", "Magnitude values"),
                    elementSerializer = { (idx, _, value) -> listOf(idx.toString(), value.toString()) }
            )
            .evaluate()
}