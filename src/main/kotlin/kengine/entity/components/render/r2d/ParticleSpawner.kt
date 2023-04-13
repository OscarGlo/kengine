package kengine.entity.components.render.r2d

import kengine.entity.components.Transform
import kengine.math.Color
import kengine.math.Rect
import kengine.math.Vector2f
import kengine.math.nextFloatSigned
import kengine.objects.KERuntime
import kengine.util.rectIndicesN
import kengine.util.rectVertices
import kotlin.random.Random

class ParticleSpawner(
    var delay: Double,
    var lifespan: Double,
    var size: Float,
    var startColor: Color,
    var initialVelocity: Vector2f,
    var acceleration: Vector2f,
    var area: Rect = Rect.zero(),
    var active: Boolean = true,
    var delayDelta: Double = 0.0,
    var lifespanDelta: Double = 0.0,
    var sizeDelta: Float = 0f,
    var endColor: Color = startColor,
    var initialVelocityDelta: Vector2f = Vector2f()
) : Render2D(floatArrayOf(), intArrayOf()) {
    class Particle(
        val birth: Double,
        val lifespan: Double,
        val size: Vector2f,
        val position: Vector2f,
        val velocity: Vector2f,
    ) {
        var age = 0f
    }

    private var spawnTime = 0.0
    private val particles = mutableListOf<Particle>()

    private fun addParticle(time: Double) {
        particles.add(
            Particle(
                time,
                lifespan + Random.nextFloatSigned() * lifespanDelta,
                Vector2f(size + Random.nextFloatSigned() * sizeDelta),
                Vector2f(entity.get<Transform>().position) + area.randomPoint(),
                initialVelocity + Vector2f.randomSigned() * initialVelocityDelta
            )
        )
    }

    override fun update(delta: Double, time: Double) {
        val tMin = spawnTime + delay
        if (active && time >= tMin) {
            spawnTime = tMin + Random.nextFloatSigned() * delayDelta
            addParticle(time)
        }

        particles.forEach {
            it.age = ((time - it.birth) / it.lifespan).toFloat()

            it.position.add(it.velocity * delta.toFloat())
            it.velocity.add(acceleration * delta.toFloat())
        }

        particles.removeIf { it.age > 1f }

        arrayBuffer.store(particles.fold(floatArrayOf()) { acc, p -> acc + rectVertices(Vector2f(p.size), p.position) })
        elementBuffer.store(rectIndicesN(particles.size))
    }

    override fun model() = KERuntime.scene.view(false)

    override fun renderSteps() {
        particles.forEach {
            colored(2, startColor * (1 - it.age) + endColor * it.age)
        }
    }
}