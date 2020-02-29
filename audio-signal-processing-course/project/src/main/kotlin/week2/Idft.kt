package week2

import evaluate
import io.wavebeans.lib.io.input
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.math.*
import io.wavebeans.lib.stream.merge
import io.wavebeans.lib.stream.window.window
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun genReversedComplexSineAt(k: Int, n: Int, at: Int): ComplexNumber {
    val x = at * 2.0 * PI * k / n
    return cos(x) + sin(x).i
}

fun main() {
    val outputFile = "idft.csv"

    val x = listOf(1, 1, 1, 1)
    val n = x.size
    val xx = (0 until n).map { x }.flatten()
    val indices = (0 until n).toList()

    xx.input()
            .window(n) { 0 }
            .merge(indices.input()) { (x, i) ->
                requireNotNull(x)
                requireNotNull(i)
                val n = x.elements.size
                x.elements.indices.map { k ->
                    x.elements[k] * genReversedComplexSineAt(k, n, i)
                }.reduce { acc, a -> acc + a } / n
            }
            .toCsv(
                    uri = "file://${File(outputFile).absolutePath}",
                    header = listOf("idx", "DFT value"),
                    elementSerializer = { (idx, _, value) -> listOf(idx.toString(), value.toString()) }
            )
            .evaluate()
}