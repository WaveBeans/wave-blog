#!/bin/bash
# home directory of WaveBeans CLI tool
# assuming it is already defined globally via `export` or you can define it here
#WAVEBEANS_CLI_HOME=

INPUT_FILE=$(pwd)/piano.wav

# generate script file, it is just more convenient this way, however you may always specify it as inline script
cat > script.kts <<EOF
val m = 2
val hopSamples = wave("file://${INPUT_FILE}")
        .trim(1)
        .window(m)
        .map { it.elements.last() }
        .asSequence(44100.0f)
        .toList()
println(hopSamples)
EOF

# run the cli tool
$WAVEBEANS_CLI_HOME/bin/wavebeans --execute-file $(pwd)/script.kts --verbose

rm -f script.kts