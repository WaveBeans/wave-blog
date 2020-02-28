package week2

import evaluate
import io.wavebeans.lib.BeanStream
import io.wavebeans.lib.io.input
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.math.ComplexNumber
import io.wavebeans.lib.math.plus
import io.wavebeans.lib.math.r
import io.wavebeans.lib.math.times
import io.wavebeans.lib.stream.SampleCountMeasurement
import io.wavebeans.lib.stream.map
import io.wavebeans.lib.stream.merge
import io.wavebeans.lib.stream.rangeProjection
import io.wavebeans.lib.stream.window.window
import java.io.File


fun main() {
    val outputFile = "dft.csv"

    val x = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    val n = 4

    SampleCountMeasurement.registerType(List::class) { it.size }
    dft(n, x)
            .rangeProjection(0, x.size * 1000L)
            .toCsv(
                    uri = "file://${File(outputFile).absolutePath}",
                    header = listOf("idx", "DFT value"),
                    elementSerializer = { (idx, _, value) -> listOf(idx.toString(), value.toString()) }
            )
            .evaluate(1.0f)
}

fun dft(n: Int, x: List<Number>): BeanStream<List<ComplexNumber>> {
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
