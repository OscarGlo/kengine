package kengine.util

import kengine.math.Vector3f

val AudioContext = js("window.AudioContext || window.webkitAudioContext")
val audioContext = js("new AudioContext()")

actual fun alListenerPosition(position: Vector3f) {
    val listener = audioContext.listener

    listener.positionX.value = position.x
    listener.positionY.value = position.y
    listener.positionZ.value = position.z
}