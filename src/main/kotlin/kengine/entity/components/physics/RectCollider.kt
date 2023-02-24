package kengine.entity.components.physics

import kengine.math.Vector2f
import kengine.math.Vector3f

class RectCollider(val size: Vector2f, position: Vector2f = Vector2f()) : Collider2D(position) {
    override fun collidingOne(other: Collider2D) = when (other) {
        is RectCollider -> {
            val p0 = globalPosition()
            val s0 = size / 2f
            val p1 = other.globalPosition()
            val s1 = other.size / 2f

            p0.x - s0.x <= p1.x + s1.x && p0.x + s0.x >= p1.x - s1.x &&
            p0.y - s0.y <= p1.y + s1.y && p0.y + s0.y >= p1.y - s1.y
        }
        is CircleCollider -> {
            val dir = other.globalPosition() - globalPosition()
            val rectSize = Vector3f(size / 2f)
            dir.max(globalPosition() - rectSize).min(globalPosition() + rectSize)

            (globalPosition() + dir - other.globalPosition()).length() < other.radius
        }
        else -> false
    }
}