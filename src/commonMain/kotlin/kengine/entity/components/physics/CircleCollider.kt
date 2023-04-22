package kengine.entity.components.physics

import kengine.math.Vector2f

class CircleCollider(private val radius: Float, offset: Vector2f = Vector2f()) : Collider2D(emptyList(), offset) {
    override fun getMinMaxProjection(axis: Vector2f): Pair<Float, Float> {
        val proj = globalPosition() proj axis
        return (proj - radius) to (proj + radius)
    }

    // TODO: Fix circle-square corner collisions
    override fun collideOne(other: Collider2D): Collision? =
        if (other is CircleCollider) {
            val axis = (other.globalPosition() - globalPosition()).normalize()
            val sep = separation(other, axis)

            if (sep <= 0) Collision(axis, sep)
            else null
        }
        else null
}