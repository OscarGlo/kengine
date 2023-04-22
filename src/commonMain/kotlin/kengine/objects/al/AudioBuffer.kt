package kengine.objects.al

import kengine.objects.Buffer
import kengine.util.alFormat
import org.lwjgl.openal.AL10.alBufferData
import org.lwjgl.openal.AL10.alGenBuffers

class AudioBuffer : Buffer<Vorbis>() {
    override fun gen() = alGenBuffers()

    override fun store(bufferData: Vorbis) =
        alBufferData(
            id,
            alFormat(bufferData.channels, bufferData.samples),
            bufferData.pcm,
            bufferData.sampleRate
        )
}