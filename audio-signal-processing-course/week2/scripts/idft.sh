#!/bin/bash
# home directory of WaveBeans CLI tool
# assuming it is already defined globally via `export` or you can define it here
#WAVEBEANS_CLI_HOME=

OUTPUT_FILE=$(pwd)/idft.csv

# generate script file, it is just more convenient this way, however you may always specify it as inline script
cat > script.kts <<EOF
import kotlin.math.*

fun genReversedComplexSineAt(k: Int, n: Int, at: Int): ComplexNumber {
    val x = at * 2.0 * PI * k / n
    return cos(x) + sin(x).i
}

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
                uri = "file://$OUTPUT_FILE",  
                header = listOf("idx", "DFT value"), 
                elementSerializer = { (idx, _, value) -> 
                        listOf(idx.toString(), value.toString())   
                }
        )
        .out()
EOF

# run the cli tool
$WAVEBEANS_CLI_HOME/bin/wavebeans --execute-file $(pwd)/script.kts --verbose --sample-rate 1

rm -f script.kts