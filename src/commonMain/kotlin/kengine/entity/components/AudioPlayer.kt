package kengine.entity.components

import kengine.entity.Entity
import kengine.math.Vector3f
import kengine.objects.al.AudioBuffer
import kengine.objects.al.Source
import kengine.objects.al.Vorbis
import kengine.util.Resource

class AudioPlayer(
    val file: Resource? = null,
    val autoplay: Boolean = false,
    relative: Boolean = false,
    position: Vector3f = Vector3f(),
    reference: Float = 1f,
    gain: Float = 1f,
    pitch: Float = 1f,
    loop: Boolean = false
) : Entity.Component() {
    val source = Source(relative, position, reference, gain, pitch, loop)

    override suspend fun init() {
        source.init()

        if (file != null)
            load(file, autoplay)
    }

    suspend fun load(resource: Resource, autoplay: Boolean = false) {
        val buf = AudioBuffer().apply {
            init()
            store(Vorbis(resource))
        }
        source.load(buf)

        if (autoplay) source.play()
    }

    override fun update(delta: Double) {
        if (source.relative)
            source.position = entity.get<Transform>().global().position
    }

    private var wasPlaying = false

    fun onTogglePause(evt: Entity.TogglePause) {
        if (evt.paused) {
            wasPlaying = source.playing
            source.pause()
        } else {
            if (wasPlaying)
                source.play()
        }
    }
}