package kengine.objects.al

import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename
import kengine.util.terminateError
import java.net.URL
import java.nio.ShortBuffer

class Vorbis(url: URL) {
    val pcm: ShortBuffer
    val channels: Int
    val sampleRate: Int
    val samples = 16

    init {
        val channelsPtr = BufferUtils.createIntBuffer(1)
        val sampleRatePtr = BufferUtils.createIntBuffer(1)
        val dataPtr = BufferUtils.createPointerBuffer(1)

        val sampleCount = stb_vorbis_decode_filename(
            url.path.replace("/", "\\").substring(1),
            channelsPtr, sampleRatePtr, dataPtr
        )
        if (sampleCount == -1) terminateError("Could not load samples from ${url.path}")

        channels = channelsPtr.get()
        sampleRate = sampleRatePtr.get()
        pcm = dataPtr.getShortBuffer(sampleCount * channels)
    }
}