package entity.components

import entity.Entity
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import util.roundTransform
import util.terminateError
import util.times

open class Transform2D : Entity.Component() {
    companion object {
        val axis = Vector3f(0f, 0f, 1f)
    }

    private var matrix = Matrix4f()

    fun global(): Matrix4f =
        if (entity.parent != null && entity.has<Transform2D>())
            matrix * entity.parent!!.get<Transform2D>().global()
        else matrix

    open fun rootViewport(): Matrix4f = entity.parent?.get<Transform2D>()?.rootViewport()
        ?: terminateError("No root transform for entity ${entity.path()}")

    fun viewport() = rootViewport() * global().roundTransform()

    fun set(m: Matrix4f) = apply { matrix.set(m) }

    fun scale(xy: Float) = apply { matrix.scale(xy, xy, 0f) }
    fun scale(scale: Vector2f) = apply { matrix.scaleXY(scale.x, scale.y) }

    fun rotate(a: Float) = apply { matrix.rotate(a, axis) }

    fun translate(offset: Vector2f) = apply { matrix.translate(offset.x, offset.y, 0f) }
}