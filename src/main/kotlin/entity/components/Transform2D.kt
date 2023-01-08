package entity.components

import entity.Entity
import org.joml.Matrix4f
import org.joml.Vector3f
import util.times

open class Transform2D : Entity.Component() {
    private var matrix = Matrix4f()

    fun global(): Matrix4f =
        if (entity.parent != null && entity.has<Transform2D>())
            matrix * entity.parent!!.get<Transform2D>().global()
        else matrix

    fun set(m: Matrix4f) = apply { matrix.set(m) }

    fun scale(xy: Float) = apply { matrix.scale(xy, xy, 0f) }
    fun scale(x: Float, y: Float) = apply { matrix.scale(x, y, 0f) }

    fun rotate(a: Float) = apply { matrix.rotate(a, Vector3f(0f, 0f, 1f)) }

    fun translate(x: Float, y: Float) = apply { matrix.translate(x, y, 0f) }
}