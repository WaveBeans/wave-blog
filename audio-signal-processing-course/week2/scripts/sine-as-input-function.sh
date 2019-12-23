#!/bin/bash
# home directory of WaveBeans CLI tool
# assuming it is already defined globally via `export` or you can define it here
#WAVEBEANS_CLI_HOME=

OUTPUT_FILE=$(pwd)/sine.csv

# generate script file, it is just more convenient this way, however you may always specify it as inline script
cat > script.kts <<EOF
import kotlin.math.*

val amplitude = 1.0
val frequency = 10.0
val phi = 1.0
val offset = phi / frequency / (PI * 2.0)
input {x, sampleRate -> sampleOf(amplitude * cos((x / sampleRate + offset) * 2 * PI * frequency))}
    .trim(100)
    .toCsv("file://${OUTPUT_FILE}")
    .out()
EOF

# run the cli tool
$WAVEBEANS_CLI_HOME/bin/wavebeans --execute-file $(pwd)/script.kts --verbose --sample-rate 50

rm -f script.kts