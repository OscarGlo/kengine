package kengine.entity.components.physics

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.entity.components.physics.Collider2D.Collision
import kengine.math.Vector2f
import kengine.math.Vector3f
import kengine.objects.KERuntime

class Body2D(val static: Boolean = false, val layers: List<Any> = listOf(0)) : Entity.Component() {
    companion object {
        const val TICKS_PER_FRAME = 16
    }

    var velocity = Vector2f()

    private lateinit var transform: Transform
    private lateinit var colliders: List<Collider2D>

    override fun initialize() {
        transform = entity.get<Transform>()
        colliders = entity.getAll<Collider2D>()
    }

    private fun getCollisions() = mutableListOf<Collision>().apply {
        colliders.forEach { collider ->
            KERuntime.root.forEachComponentRec<Collider2D> {
                val body = it.entity.get<Body2D>()
                if (it.entity != entity && body.layers.intersect(layers).isNotEmpty()) {
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
                transform.translate(tickVelocity)

                val collisions = getCollisions()

                if (collisions.isNotEmpty()) {
                    val col = collisions.maxBy { it.separation }
                    transform.translate(-Vector3f(col.axis) * col.separation)
                }
            }
        }
    }
}
