package week2

import evaluate
import io.wavebeans.lib.Fn
import io.wavebeans.lib.FnInitParameters
import io.wavebeans.lib.io.input
import io.wavebeans.lib.io.toCsv
import io.wavebeans.lib.math.ComplexNumber
import io.wavebeans.lib.math.i
import io.wavebeans.lib.math.minus
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class GenerateComplexSineFn(params: FnInitParameters) : Fn<Pair<Long, Float>, ComplexNumber?>(params) {

    constructor(k: Int, n: Int) : this(FnInitParameters().add("n", n).add("k", k))

    override fun apply(argument: Pair<Long, Float>): ComplexNumber? {
        val n = initParams.int("n")
        val k = initParams.int("k").toDouble()
        val i = argument.first
        return if (i < n) {
            val x = i * 2.0 * PI * k / n
            cos(x) - sin(x).i
        } else {
            null
        }
    }

}

fun main() {
    val outputFile = "complex-sine.csv"

    val k = 1
    val n = 5

    input(GenerateComplexSineFn(k, n))
            .toCsv(
                    uri = "file://${File(outputFile).absolutePath}",
                    header = listOf("index", "value"),
                    elementSerializer = { (idx, _, value) ->
                        listOf(idx.toString(), value.toString())
                    }
            )
            .evaluate()
}