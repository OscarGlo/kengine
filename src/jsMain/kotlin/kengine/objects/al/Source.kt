package kengine.objects.al

import kengine.math.Vector3f
import kengine.util.audioContext

// TODO: Spatial audio
actual class Source actual constructor(
    actual var relative: Boolean,
    actual var position: Vector3f,
    actual var reference: Float,
    gain: Float,
    pitch: Float,
    loop: Boolean
) {
    private var source: dynamic = null
    private var gainNode: dynamic = null

    actual fun init() {
        source = audioContext.createBufferSource()
        gainNode = audioContext.createGainNode()
        source.connect(gainNode).connect(audioContext.destination)
    }

    actual var gain = gain
        set(g) {
            field = g
            gainNode.gain.value = g
        }

    actual var pitch = pitch
        set(p) {
            field = p
            source.playbackRate = p
        }

    actual var loop = loop
        set(l) {
            field = l
            source.loop = l
        }

    actual fun load(buffer: AudioBuffer) = apply {
        source.buffer = buffer
    }

    private var _playing = false
    actual val playing = _playing

    actual fun play() {
        _playing = true
        source.start()
    }

    // TODO: Fix pausing
    actual fun pause() = stop()

    actual fun stop() {
        _playing = false
        source.stop()
    }
}