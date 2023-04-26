package kengine.objects.al

import kengine.util.audioContext
import kotlinx.coroutines.await
import kotlin.js.Promise

actual class AudioBuffer {
    var buffer: dynamic = null

    actual fun init() {}

    actual suspend fun store(bufferData: Vorbis) {
        buffer = (audioContext.decodeAudioData(bufferData.arrayBuffer) as Promise<dynamic>).await()
    }
}