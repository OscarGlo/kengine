package kengine.objects.al

import kengine.math.Vector3f
import kengine.util.alBool
import org.lwjgl.openal.AL10.*

class Source(gain: Float = 1f, pitch: Float = 1f, position: Vector3f = Vector3f(), loop: Boolean = false) {
    val id = alGenSources()

    fun init() {
        alSourcef(id, AL_GAIN, gain)
        alSourcef(id, AL_PITCH, pitch)
        alSource3f(id, AL_POSITION, position.x, position.y, position.z)
        alSourcei(id, AL_LOOPING, alBool(loop))
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

    var position = position
        set(pos) {
            field = pos
            alSource3f(id, AL_POSITION, position.x, position.y, position.z)
        }

    var loop = loop
        set(l) {
            field = l
            alSourcei(id, AL_LOOPING, alBool(l))
        }

    fun load(buffer: Audio) = apply { alSourcei(id, AL_BUFFER, buffer.id) }

    fun play() = apply { alSourcePlay(id) }
    fun pause() = apply { alSourcePause(id) }
    fun rewind() = apply { alSourceRewind(id) }
    fun stop() = apply { alSourceStop(id) }
}