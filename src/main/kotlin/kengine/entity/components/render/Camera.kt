package kengine.entity.components.render

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.math.*
import kengine.util.Event
import kotlin.math.tan
import kotlin.reflect.KClass

class Camera(
    current: Boolean = false,
    var fov: Float = 90f,
    var near: Float = 1f,
    var far: Float = 1000f,
    private val customTransform: Transform = Transform(),
) : Entity.Component() {
    class SetCurrentEvent(val camera: Camera?) : Event()

    override fun initialize() {
        if (current)
            root.update(SetCurrentEvent(this))
    }

    override val required: List<KClass<out Entity.Component>> = listOf(Transform::class)

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

    val front get() = Vector3f(entity.get<Transform>().global() * Vector4f(Vector3f.front, 0f))
    val right get() = Vector3f(entity.get<Transform>().global() * Vector4f(Vector3f.right, 0f))
    val up    get() = Vector3f(entity.get<Transform>().global() * Vector4f(Vector3f.up, 0f))

    fun projection(): Matrix4 {
        val tFov = tan(fov / 2)
        val dist = -(far + near) / (far - near)
        return Matrix4(
            mutableListOf(
                mutableListOf(1 / (root.window.aspect * tFov), 0f, 0f, 0f),
                mutableListOf(0f, 1 / tFov, 0f, 0f),
                mutableListOf(0f, 0f, dist, 2 * dist),
                mutableListOf(0f, 0f, -1f, 0f)
            )
        )
    }

    fun view() = entity.get<Transform>().run {
        customTransform.matrix * Matrix4(
            mutableListOf(
                mutableListOf(right.x, right.y, right.z, -(right dot position)),
                mutableListOf(up.x,    up.y,    up.z,    -(up    dot position)),
                mutableListOf(front.x, front.y, front.z, -(front dot position)),
                mutableListOf(0f,      0f,      0f,      1f                   )
            )
        )
    }
}