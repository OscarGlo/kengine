package kengine.entity.components.physics

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import kengine.entity.components.physics.Collider2D.*
import kengine.math.Vector2f
import kengine.math.Vector3f

class Body2D(val static: Boolean = false) : Entity.Component() {
    companion object {
        const val TICKS_PER_FRAME = 16
    }

    var velocity = Vector2f()

    private lateinit var transform: Transform2D
    private lateinit var colliders: List<Collider2D>

    override fun initialize() {
        transform = entity.get<Transform2D>()
        colliders = entity.getAll<Collider2D>()
    }

    private fun getCollisions() = mutableListOf<Collision>().apply {
        colliders.forEach { collider ->
            root.forEachComponentRec<Collider2D> {
                if (it.entity != entity) {
                    val col = collider.collide(it)
                    if (col != null)
                        this += col
                }
            }
        }
    }

    // TODO: Framerate independent physics
    fun physicsUpdate(delta: Double) {
        if (!static) {
            val tickVelocity = Vector3f(velocity) * (delta.toFloat() / TICKS_PER_FRAME)

            for (i in 0..TICKS_PER_FRAME) {
                transform.matrix.translate(tickVelocity)

                val collisions = getCollisions()

                if (collisions.isNotEmpty()) {
                    val col = collisions.maxBy { it.separation }
                    transform.matrix.translate(-Vector3f(col.axis) * col.separation)
                }
            }
        }
    }
}
