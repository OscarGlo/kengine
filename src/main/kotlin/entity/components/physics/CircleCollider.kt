package entity.components.physics

import org.joml.Vector2f

class CircleCollider(val radius: Float, position: Vector2f = Vector2f()) : Collider2D(position) {
    override fun colliding(other: Collider2D) = when (other) {
        is CircleCollider -> globalPosition().distance(other.globalPosition()) <= radius + other.radius
        else -> false
    }
}