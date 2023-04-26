package kengine.objects.al

import kengine.util.alFormat
import org.lwjgl.openal.AL10.alBufferData
import org.lwjgl.openal.AL10.alGenBuffers
import kotlin.properties.Delegates

actual class AudioBuffer {
    var id by Delegates.notNull<Int>()

    actual fun init() {
        id = alGenBuffers()
    }

    actual suspend fun store(bufferData: Vorbis) =
        alBufferData(
            id,
            alFormat(bufferData.channels, bufferData.samples),
            bufferData.pcm,
            bufferData.sampleRate
        )
}