package objects.al

import objects.Buffer
import org.lwjgl.openal.AL10.*
import util.alFormat

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