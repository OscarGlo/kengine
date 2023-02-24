package kengine.entity.components.physics

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import kengine.math.Vector2f
import kengine.math.Vector3f

class Body2D(val static: Boolean = false) : Entity.Component() {
    var velocity = Vector2f()

    private lateinit var transform: Transform2D

    override fun initialize() {
        transform = entity.get<Transform2D>()
    }

    fun physicsUpdate(delta: Double) {
        if (!static) {
            val frameVelocity = Vector3f(velocity) * delta.toFloat()
            transform.matrix.translate(frameVelocity)
        }
    }
}
