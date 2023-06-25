package kengine.entity.components.render

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.math.Matrix4
import kengine.math.Vector3f
import kengine.math.Vector4f
import kengine.objects.KERuntime
import kengine.tools.Event
import org.lwjgl.openal.AL10.AL_POSITION
import org.lwjgl.openal.AL10.alListener3f
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

    override fun update(delta: Double) {
        if (current) {
            val pos = entity.get<Transform>().global().position
            alListener3f(AL_POSITION, pos[0], pos[1], pos[2])
        }
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

    @Event.Listener
    fun onCameraChange(evt: SetCurrentEvent) {
        _current = evt.camera == this
    }

    val front get() = Vector3f(entity.get<Transform>().global() * Vector4f(Vector3f.front, 0f))
    val right get() = Vector3f(entity.get<Transform>().global() * Vector4f(Vector3f.right, 0f))
    val up    get() = Vector3f(entity.get<Transform>().global() * Vector4f(Vector3f.up,    0f))

    fun projection(): Matrix4 {
        val tFov = tan(fov / 2)
        val dist = -(far + near) / (far - near)
        return Matrix4(
            1 / (KERuntime.window.aspect * tFov), 0f, 0f, 0f,
            0f, 1 / tFov, 0f, 0f,
            0f, 0f, dist, -1f,
            0f, 0f, 2 * dist, 0f
        )
    }

    fun view() = entity.get<Transform>().global().let {
        val pos = Vector3f(it[0, 3], it[1, 3], it[2, 3])
        customTransform * Matrix4(
            right.x,          up.x,          front.x,          0f,
            right.y,          up.y,          front.y,          0f,
            right.z,          up.z,          front.z,          0f,
            -(right dot pos), -(up dot pos), -(front dot pos), 1f
        )
    }
}