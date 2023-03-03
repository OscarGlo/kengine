package kengine.entity.components.physics

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import kengine.math.Vector2f
import kengine.math.Vector3f

class Body2D(val static: Boolean = false) : Entity.Component() {
    var velocity = Vector2f()

    private lateinit var transform: Transform2D
    private lateinit var colliders: List<Collider2D>

    override fun initialize() {
        transform = entity.get<Transform2D>()
        colliders = entity.getAll<Collider2D>()
    }

    // TODO: Framerate independent physics
    fun physicsUpdate(delta: Double) {
        if (!static) {
            val frameVelocity = Vector3f(velocity) * delta.toFloat()
            transform.matrix.translate(frameVelocity)

            // Calculate collisions
            val collisions = mutableListOf<Collider2D.Collision>()
            colliders.forEach { collider ->
                root.forEachComponentRec<Collider2D> {
                    if (it.entity != entity) {
                        val col = collider.collide(it)
                        if (col != null)
                            collisions += col
                    }
                }
            }

            // Resolve collisions
            if (collisions.isNotEmpty()) {
                val col = collisions.minBy { it.distance }
                transform.matrix.translate(-Vector3f(col.axis) * col.distance)
            }
        }
    }
}
