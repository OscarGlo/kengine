package kengine.objects.al

import kengine.objects.Buffer
import org.lwjgl.openal.AL10.*
import kengine.objects.util.alFormat

class Audio : Buffer<Vorbis>() {
    override fun gen() = alGenBuffers()

    override fun store(bufferData: Vorbis) =
        alBufferData(
            id,
            alFormat(bufferData.channels, bufferData.samples),
            bufferData.pcm,
            bufferData.sampleRate
        )
}