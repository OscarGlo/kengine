package kengine.entity.components.physics

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import org.joml.Vector2f
import org.joml.Vector3f

abstract class Collider2D(private val position: Vector2f) : Entity.Component() {
    fun globalPosition(): Vector3f = entity.get<Transform2D>().global()
        .translate(position.x, position.y, 0f)
        .transformPosition(Vector3f())

    protected abstract fun collidingOne(other: Collider2D): Boolean
    fun colliding(other: Collider2D) = this.collidingOne(other) || other.collidingOne(this)
}