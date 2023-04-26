package kengine.objects.al

import kengine.math.Vector3f
import kengine.util.boolInt
import org.lwjgl.openal.AL10.*
import kotlin.properties.Delegates

actual class Source actual constructor(
    relative: Boolean,
    position: Vector3f,
    reference: Float,
    gain: Float,
    pitch: Float,
    loop: Boolean
) {
    var id by Delegates.notNull<Int>()

    actual fun init() {
        id = alGenSources()

        alSourcei(id, AL_SOURCE_RELATIVE, boolInt(!relative))
        alSource3f(id, AL_POSITION, position.x, position.y, position.z)
        alSourcef(id, AL_REFERENCE_DISTANCE, reference)
        alSourcef(id, AL_GAIN, gain)
        alSourcef(id, AL_PITCH, pitch)
        alSourcei(id, AL_LOOPING, boolInt(loop))
    }

    actual var relative = relative
        set(r) {
            field = r
            alSourcei(id, AL_SOURCE_RELATIVE, boolInt(!relative))
        }

    actual var position = position
        set(pos) {
            field = pos
            alSource3f(id, AL_POSITION, position.x, position.y, position.z)
        }

    actual var reference = reference
        set(r) {
            field = r
            alSourcef(id, AL_REFERENCE_DISTANCE, r)
        }

    actual var gain = gain
        set(g) {
            field = g
            alSourcef(id, AL_GAIN, g)
        }

    actual var pitch = pitch
        set(p) {
            field = p
            alSourcef(id, AL_PITCH, p)
        }

    actual var loop = loop
        set(l) {
            field = l
            alSourcei(id, AL_LOOPING, boolInt(l))
        }

    actual fun load(buffer: AudioBuffer) = apply { alSourcei(id, AL_BUFFER, buffer.id) }

    private var _playing = false
    actual val playing = _playing

    actual fun play() {
        _playing = true
        alSourcePlay(id)
    }

    actual fun pause() {
        _playing = false
        alSourcePause(id)
    }

    actual fun stop() {
        _playing = false
        alSourceStop(id)
    }
}