#!/bin/bash
# home directory of WaveBeans CLI tool
# assuming it is already defined globally via `export` or you can define it here
#WAVEBEANS_CLI_HOME=

OUTPUT_FILE=$(pwd)/dft.csv

# generate script file, it is just more convenient this way, however you may always specify it as inline script
cat > script.kts <<EOF
import kotlin.math.*

class GenerateComplexSineFn(params: FnInitParameters)                
: Fn<Pair<Long, Float>, ComplexNumber?>(params) {                    

    constructor(k: Int, n: Int) : this(                              
            FnInitParameters().add("n", n).add("k", k)               
        )

    override fun apply(argument: Pair<Long, Float>): ComplexNumber? {
        val n = initParams.int("n")           
        val k = initParams.int("k").toDouble()
        val i = argument.first            
        val x = (i % n) * 2.0 * PI * k / n
        return cos(x) - sin(x).i
    }
}

val x = listOf(1, 2, 3, 4)                                   
val n = 4                                                    

SampleCountMeasurement.registerType(ComplexNumber::class) { 1 }

val signal = x.input()                                     
(0 until n).map { k ->                                     
        input(GenerateComplexSineFn(k, n))                 
                .merge(signal) { (sine, x) ->              
                        requireNotNull(sine)               
                        requireNotNull(x)                  
                        x * sine                           
                }
                .window(n) { 0.r }                         
                .map {                                     
                        listOf(it.elements.reduce { a, b ->
                                a + b                      
                        })                                    
                }
}
        .reduce { acc, s ->                                
                acc.merge(s) { (a, b) ->                   
                        requireNotNull(a)                  
                        requireNotNull(b)                  
                        a + b                              
                }
        }
        .rangeProjection(0, x.size * 1000L)                
        .toCsv(                                            
                uri = "file://$OUTPUT_FILE",
                header = listOf("idx", "DFT value"),
                elementSerializer = { (idx, _, value) ->
                   listOf(idx.toString(), value.toString())
                }
        )
        .out()
EOF

# run the cli tool
$WAVEBEANS_CLI_HOME/bin/wavebeans --execute-file $(pwd)/script.kts --verbose --sample-rate 50

rm -f script.kts