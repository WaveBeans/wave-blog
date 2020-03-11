#!/bin/bash
# home directory of WaveBeans CLI tool
# assuming it is already defined globally via `export` or you can define it here
#WAVEBEANS_CLI_HOME=

OUTPUT_FILE=$(pwd)/complex-sine.csv

# generate script file, it is just more convenient this way, however you may always specify it as inline script
cat > script.kts <<EOF
import kotlin.math.*

class GenerateComplexSineFn(params: FnInitParameters): Fn<Pair<Long, Float>, ComplexNumber?>(params) {                       

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

val k = 1 
val n = 5 
    
SampleCountMeasurement.registerType(ComplexNumber::class) { 1 } 

input(GenerateComplexSineFn(k, n))                            
        .rangeProjection(0, 5000)                                     
        .toCsv(                                                       
                uri = "file://$OUTPUT_FILE",                          
                header = listOf("index", "value"),                    
                elementSerializer = { (idx, _, value) ->              
                        listOf(idx.toString(), value.toString())      
                }
        )
        .out()
EOF

# run the cli tool
$WAVEBEANS_CLI_HOME/bin/wavebeans --execute-file $(pwd)/script.kts --verbose --sample-rate 1

rm -f script.kts