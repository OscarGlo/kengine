package kengine.objects.al

import kengine.math.Vector3f
import kengine.util.boolInt
import org.lwjgl.openal.AL10.*
import kotlin.properties.Delegates

class Source(
    relative: Boolean = false,
    position: Vector3f = Vector3f(),
    reference: Float = 1f,
    gain: Float = 1f,
    pitch: Float = 1f,
    loop: Boolean = false
) {
    var id by Delegates.notNull<Int>()

    fun init() {
        id = alGenSources()

        alSourcei(id, AL_SOURCE_RELATIVE, boolInt(!relative))
        alSource3f(id, AL_POSITION, position.x, position.y, position.z)
        alSourcef(id, AL_REFERENCE_DISTANCE, reference)
        alSourcef(id, AL_GAIN, gain)
        alSourcef(id, AL_PITCH, pitch)
        alSourcei(id, AL_LOOPING, boolInt(loop))
    }

    var relative = relative
        set(r) {
            field = r
            alSourcei(id, AL_SOURCE_RELATIVE, boolInt(!relative))
        }

    var position = position
        set(pos) {
            field = pos
            alSource3f(id, AL_POSITION, position.x, position.y, position.z)
        }

    var reference = reference
        set(r) {
            field = r
            alSourcef(id, AL_REFERENCE_DISTANCE, r)
        }

    var gain = gain
        set(g) {
            field = g
            alSourcef(id, AL_GAIN, g)
        }

    var pitch = pitch
        set(p) {
            field = p
            alSourcef(id, AL_PITCH, p)
        }

    var loop = loop
        set(l) {
            field = l
            alSourcei(id, AL_LOOPING, boolInt(l))
        }

    fun load(buffer: AudioBuffer) = apply { alSourcei(id, AL_BUFFER, buffer.id) }

    var playing = false; private set

    fun play() = apply {
        playing = true
        alSourcePlay(id)
    }

    fun pause() = apply {
        playing = false
        alSourcePause(id)
    }

    fun rewind() = apply {
        playing = false
        alSourceRewind(id)
    }

    fun stop() = apply {
        playing = false
        alSourceStop(id)
    }
}