package kengine.entity.components.physics

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import org.joml.Vector2f

class Body2D(val static: Boolean = false) : Entity.Component() {
    var velocity = Vector2f()

    private lateinit var transform: Transform2D

    override fun initialize() {
        transform = entity.get<Transform2D>()
    }

    fun physicsUpdate(delta: Long) {
        if (!static) {
            val frameVelocity = velocity.mul(delta / 1000f, Vector2f())
            transform.translate(frameVelocity)
        }
    }
}
