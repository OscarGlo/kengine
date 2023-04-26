package kengine.objects.al

import kengine.math.Vector3f

expect class Source(
    relative: Boolean = false,
    position: Vector3f = Vector3f(),
    reference: Float = 1f,
    gain: Float = 1f,
    pitch: Float = 1f,
    loop: Boolean = false
) {
    fun init()

    var relative: Boolean
    var position: Vector3f
    var reference: Float
    var gain: Float
    var pitch: Float
    var loop: Boolean

    fun load(buffer: AudioBuffer): Source

    val playing: Boolean

    fun play()
    fun pause()
    fun stop()
}