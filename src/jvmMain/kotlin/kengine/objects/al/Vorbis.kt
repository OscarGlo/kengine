package kengine.objects.al

import kengine.util.Resource
import kengine.util.terminateError
import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename
import java.nio.ShortBuffer

actual class Vorbis actual constructor(resource: Resource) {
    val pcm: ShortBuffer
    val channels: Int
    val sampleRate: Int
    val samples = 16

    init {
        val channelsPtr = BufferUtils.createIntBuffer(1)
        val sampleRatePtr = BufferUtils.createIntBuffer(1)
        val dataPtr = BufferUtils.createPointerBuffer(1)

        val sampleCount = stb_vorbis_decode_filename(
            resource.path.replace("/", "\\").substring(1),
            channelsPtr, sampleRatePtr, dataPtr
        )
        if (sampleCount == -1) terminateError("Could not load samples from ${resource.path}")

        channels = channelsPtr.get()
        sampleRate = sampleRatePtr.get()
        pcm = dataPtr.getShortBuffer(sampleCount * channels)
    }
}