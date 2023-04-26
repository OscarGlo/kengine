package kengine.util

import kengine.math.Vector3f
import org.lwjgl.openal.AL10.*

actual fun alListenerPosition(position: Vector3f) = alListener3f(AL_POSITION, position.x, position.y, position.z)

fun alFormat(channels: Int, samples: Int) = when (samples) {
    16 -> when (channels) {
        1 -> AL_FORMAT_MONO16
        2 -> AL_FORMAT_STEREO16
        else -> terminateError("Invalid channel count $channels")
    }
    8 -> when (channels) {
        1 -> AL_FORMAT_MONO8
        2 -> AL_FORMAT_STEREO8
        else -> terminateError("Invalid channel count $channels")
    }
    else -> terminateError("Invalid sample count $samples")
}