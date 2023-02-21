package kengine.entity.components

import kengine.entity.Entity
import kengine.util.roundTransform
import kengine.util.terminateError
import kengine.util.times
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

open class Transform2D(var fixed: Boolean = false) : Entity.Component() {
    companion object {
        val axis = Vector3f(0f, 0f, 1f)
    }

    val matrix = Matrix4f()

    fun global(): Matrix4f =
        if (entity.parent != null && entity.has<Transform2D>())
            matrix * entity.parent!!.get<Transform2D>().global()
        else matrix

    open fun rootViewport(fixed: Boolean = false): Matrix4f =
        entity.parent?.get<Transform2D>()?.rootViewport(this.fixed || fixed)
            ?: terminateError("No root transform for entity ${entity.path()}")

    fun viewport() = rootViewport(fixed) * global().roundTransform()

    fun set(m: Matrix4f) = apply { matrix.set(m) }

    fun scale(xy: Float) = apply { matrix.scaleXY(xy, xy) }
    fun scale(scale: Vector2f) = apply { matrix.scaleXY(scale.x, scale.y) }

    fun rotate(a: Float) = apply { matrix.rotate(a, axis) }

    fun translate(offset: Vector2f) = apply { matrix.translate(offset.x, offset.y, 0f) }
}