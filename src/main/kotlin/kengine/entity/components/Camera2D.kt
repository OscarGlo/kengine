package kengine.entity.components

import kengine.entity.Entity
import kengine.math.Matrix3
import kengine.math.Matrix4
import kengine.math.Vector3f
import kengine.util.Event

class Camera2D(
    current: Boolean = false,
    private val keepScaling: Boolean = false,
    private val customTransform: Matrix4 = Matrix4(),
) : Entity.Component() {
    class SetCurrentEvent(val camera: Camera2D?) : Event()

    override fun initialize() {
        if (current)
            root.update(SetCurrentEvent(this))
    }

    private var _current = current
    var current
        get() = _current
        set(c) {
            if (c && !current) root.update(SetCurrentEvent(this))
            if (!c && current) root.update(SetCurrentEvent(null))
            _current = c
        }

    @Event.Listener(SetCurrentEvent::class)
    fun onCameraChange(evt: SetCurrentEvent) {
        _current = evt.camera == this
    }

    fun transform(): Matrix4 {
        val entityTransform = entity.get<Transform2D>().global()
        val baseTransform = entityTransform.apply {
            scaling = if (!keepScaling) Vector3f(1f) else scaling.inverse()
            rotation = Matrix3()
            position = scaling * -position
        }
        return baseTransform * customTransform
    }
}