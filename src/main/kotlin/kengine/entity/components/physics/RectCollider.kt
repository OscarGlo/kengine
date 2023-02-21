package kengine.entity.components.physics

import kengine.util.as3
import org.joml.Vector2f

class RectCollider(val size: Vector2f, position: Vector2f = Vector2f()) : Collider2D(position) {
    override fun collidingOne(other: Collider2D) = when (other) {
        is RectCollider -> {
            val p0 = globalPosition()
            val s0 = size.div(2f, Vector2f())
            val p1 = other.globalPosition()
            val s1 = other.size.div(2f, Vector2f())

            p0.x - s0.x <= p1.x + s1.x && p0.x + s0.x >= p1.x - s1.x &&
            p0.y - s0.y <= p1.y + s1.y && p0.y + s0.y >= p1.y - s1.y
        }
        is CircleCollider -> {
            val dir = other.globalPosition().sub(globalPosition())
            val rectSize = size.div(2f, Vector2f()).as3()
            dir.max(globalPosition().sub(rectSize))
                .min(globalPosition().add(rectSize))

            globalPosition().add(dir).sub(other.globalPosition()).length() < other.radius
        }
        else -> false
    }
}