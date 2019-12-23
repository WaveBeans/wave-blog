#!/bin/bash
# home directory of WaveBeans CLI tool
# assuming it is already defined globally via `export` or you can define it here
#WAVEBEANS_CLI_HOME=

OUTPUT_FILE=$(pwd)/dft.csv

# generate script file, it is just more convenient this way, however you may always specify it as inline script
cat > script.kts <<EOF
import kotlin.math.*

val amplitude = 1.0
val frequency = 10.0
val phi = 1.0
val offset = phi / frequency / (PI * 2.0)
frequency.sine(amplitude = 1.0, timeOffset = offset)
    .merge (
        with = input {x, sampleRate -> sampleOf(x / sampleRate) }
    ) { x, y -> 
        val cos = amplitude * cos((y + offset) * 2 * PI * frequency)
        val sin = amplitude * sin((y + offset) * 2 * PI * frequency)
        val complexSine = cos + sin.i

        x * y
    }
    .trim(100)
    .toCsv("file://${OUTPUT_FILE}")
    .out()
EOF

# run the cli tool
$WAVEBEANS_CLI_HOME/bin/wavebeans --execute-file $(pwd)/script.kts --verbose --sample-rate 50

rm -f script.kts