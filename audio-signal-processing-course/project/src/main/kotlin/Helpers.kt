import io.wavebeans.execution.LocalOverseer
import io.wavebeans.lib.io.StreamOutput

fun <T : Any> StreamOutput<T>.evaluate(sampleRate: Float = 44100.0f) {
    LocalOverseer(listOf(this)).use {
        if (!it.eval(sampleRate).all { it.get() }) {
            println("Problems during launching the stream")
        }
    }

}