import io.wavebeans.execution.SingleThreadedOverseer
import io.wavebeans.lib.io.StreamOutput

fun <T : Any> StreamOutput<T>.evaluate(sampleRate: Float = 44100.0f) {
    SingleThreadedOverseer(listOf(this)).use { overseer ->
        if (!overseer.eval(sampleRate).all { it.get().finished }) {
            println("Problems during launching the stream")
        }
    }

}