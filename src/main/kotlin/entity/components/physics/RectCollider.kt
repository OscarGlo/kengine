package entity.components.physics

import org.joml.Vector2f
import org.joml.Vector3f

class RectCollider(val size: Vector2f, position: Vector2f = Vector2f()) : Collider2D(position) {
    override fun colliding(other: Collider2D) = when (other) {
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
            val rectSize = Vector3f(size.x / 2f, size.y / 2f, 0f)
            dir.max(globalPosition().sub(rectSize))
                .min(globalPosition().add(rectSize))

            globalPosition().add(dir).sub(other.globalPosition()).length() < other.radius
        }
        else -> false
    }
}