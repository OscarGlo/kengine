package kengine.entity.components.render

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.math.*
import kengine.objects.KERuntime
import kengine.util.Event
import kotlin.math.tan
import kotlin.reflect.KClass

class Camera(
    current: Boolean = false,
    var fov: Float = 90f,
    var near: Float = 1f,
    var far: Float = 1000f,
    private val customTransform: Matrix4 = Matrix4(),
) : Entity.Component() {
    class SetCurrentEvent(val camera: Camera?) : Event()

    override fun initialize() {
        if (current)
            notify(SetCurrentEvent(this))
    }

    override val required: List<KClass<out Entity.Component>> = listOf(Transform::class)

    private var _current = current
    var current
        get() = _current
        set(c) {
            if (c && !current) notify(SetCurrentEvent(this))
            if (!c && current) notify(SetCurrentEvent(null))
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
                mutableListOf(1 / (KERuntime.window.aspect * tFov), 0f, 0f, 0f),
                mutableListOf(0f, 1 / tFov, 0f, 0f),
                mutableListOf(0f, 0f, dist, 2 * dist),
                mutableListOf(0f, 0f, -1f, 0f)
            )
        )
    }

    fun view() = entity.get<Transform>().global().let {
        val pos = Vector3f(it[0, 3], it[1, 3], it[2, 3])
        customTransform * Matrix4(
            mutableListOf(
                mutableListOf(right.x, right.y, right.z, -(right dot pos)),
                mutableListOf(up.x,    up.y,    up.z,    -(up    dot pos)),
                mutableListOf(front.x, front.y, front.z, -(front dot pos)),
                mutableListOf(0f,      0f,      0f,      1f                   )
            )
        )
    }
}