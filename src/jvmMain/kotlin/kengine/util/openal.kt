package kengine.util

import org.lwjgl.openal.AL10.*

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