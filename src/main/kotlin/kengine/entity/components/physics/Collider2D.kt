package kengine.entity.components.physics

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import kengine.math.Vector2f
import kengine.math.Vector3f
import kotlin.math.max

open class Collider2D(private val points: List<Vector2f>, private val offset: Vector2f = Vector2f()) :
    Entity.Component() {
    class Collision(val axis: Vector2f, val distance: Float)

    fun globalPosition(): Vector2f {
        val entityTransform = entity.get<Transform2D>().global()
        entityTransform.translate(Vector3f(offset))
        return Vector2f(entityTransform.position)
    }

    protected open fun getMinMaxProjection(axis: Vector2f): Pair<Float, Float> {
        val global = globalPosition()
        var min = Float.MAX_VALUE
        var max = -Float.MAX_VALUE

        points.forEach {
            val proj = (global + it) proj axis
            if (proj < min) min = proj
            else if (proj > max) max = proj
        }

        return min to max
    }

    protected fun separation(other: Collider2D, axis: Vector2f): Float {
        val (min1, max1) = getMinMaxProjection(axis)
        val (min2, max2) = other.getMinMaxProjection(axis)

        return max(min1 - max2, min2 - max1)
    }

    protected open fun collideOne(other: Collider2D): Collision? = points.indices
        .map { i -> (points[(i + 1) % points.size] - points[i]).perpendicular().normalize() }
        .fold(null as Collision?) { col, axis ->
            val sep = separation(other, axis)
            if (sep > 0) return null
            if (col == null || sep > col.distance) Collision(axis, sep) else col
        }?.apply {
            // Fix axis direction
            val p1 = globalPosition()
            val p2 = other.globalPosition()
            if (p1.x > p2.x && axis.x < 0 || p1.x < p2.x && axis.x > 0) axis.x = -axis.x
            if (p1.y > p2.y && axis.y < 0 || p1.y < p2.y && axis.y > 0) axis.y = -axis.y
        }

    fun collide(other: Collider2D) = this.collideOne(other) ?: other.collideOne(this)
}