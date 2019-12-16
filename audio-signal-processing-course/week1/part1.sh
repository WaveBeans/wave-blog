#!/bin/bash
# home directory of WaveBeans CLI tool
# assuming it is already defined globally via `export` or you can define it here
#WAVEBEANS_CLI_HOME=

INPUT_FILE=$(pwd)/piano.wav
OUTPUT_FILE=$(pwd)/piano.csv

# generate script file, it is just more convenient this way, however you may always specify it as inline script
cat > script.kts <<EOF
wave("file://$INPUT_FILE")
    .rangeProjection(1133, timeUnit = MILLISECONDS)
    .trim(227, timeUnit = MICROSECONDS)
    .toCsv("file://$OUTPUT_FILE")
    .out()
EOF

# run the cli tool
$WAVEBEANS_CLI_HOME/bin/wavebeans --execute-file $(pwd)/script.kts --verbose

rm -f script.kts