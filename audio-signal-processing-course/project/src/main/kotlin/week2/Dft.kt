package week2

import evaluate
import io.wavebeans.lib.BeanStream
import io.wavebeans.lib.io.input
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.math.ComplexNumber
import io.wavebeans.lib.math.plus
import io.wavebeans.lib.math.r
import io.wavebeans.lib.math.times
import io.wavebeans.lib.stream.map
import io.wavebeans.lib.stream.merge
import io.wavebeans.lib.stream.window.window
import java.io.File


fun main() {
    val outputFile = "dft.csv"

    val x = listOf(1.r, 2.r, 3.r, 4.r)
    val n = x.size

    Dft(n, x)
            .toCsv(
                    uri = "file://${File(outputFile).absolutePath}",
                    header = listOf("idx", "DFT value"),
                    elementSerializer = { (idx, _, value) -> listOf(idx.toString(), value.toString()) }
            )
            .evaluate()
}

fun Dft(n: Int, x: List<ComplexNumber>): BeanStream<List<ComplexNumber>> {
    val signal = x.input()
    return (0 until n).map { k ->
        input(GenerateComplexSineFn(k, n))
                .merge(signal) { (sine, x) ->
                    requireNotNull(sine)
                    requireNotNull(x)
                    x * sine
                }
                .window(n) { 0.r }
                .map { listOf(it.elements.reduce { a, b -> a + b }) }
    }
            .reduce { acc, s ->
                acc.merge(s) { (a, b) ->
                    requireNotNull(a)
                    requireNotNull(b)
                    a + b
                }
            }
}
