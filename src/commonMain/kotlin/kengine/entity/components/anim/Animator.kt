package kengine.entity.components.anim

import kengine.entity.Entity
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.reflect.KMutableProperty1

typealias Tangent<V> = Pair<V, Double>

object Shaping {
    val linear = { t: Double -> t }
    val smoothstep = { t: Double -> 3 * t.pow(2) - 2 * t.pow(3) }
    fun pow(n: Double) = { t: Double -> t.pow(n) }
    val sqrt = { t: Double -> sqrt(t) }
}

class Animation<T : Any>(
    val getInstance: Entity.() -> T,
    val duration: Double,
    val loop: Boolean,
    var running: Boolean,
    vararg val properties: PropertyAnimation<T, *>
) {
    lateinit var instance: T
    var time: Double = 0.0

    fun init(entity: Entity) {
        instance = getInstance(entity)
        properties.forEach { it.animation = this }
    }

    fun update(delta: Double) {
        var shouldReset = false
        time += delta
        if (time > duration) {
            if (loop) {
                time %= duration
            } else {
                // Play last step of animation
                time = duration
                shouldReset = true
            }
        }

        properties.forEach { it.update() }

        if (shouldReset) time = 0.0
    }

    fun play() {
        running = true
    }

    fun pause() {
        running = false
    }

    fun reset() {
        time = 0.0
    }

    fun start() {
        reset()
        play()
    }

    fun stop() {
        reset()
        pause()
    }
}

class PropertyAnimation<T : Any, V : Any>(
    val property: KMutableProperty1<T, V>, vararg val keyframes: Keyframe<V>
) {
    lateinit var animation: Animation<T>

    fun update() {
        val prevIndex = keyframes.foldIndexed(0) { i, acc, key -> if (key.time <= animation.time) i else acc }
        val prev = keyframes[prevIndex]

        if (!animation.loop && prevIndex == keyframes.size - 1) return property.set(animation.instance, prev.value)

        val next = keyframes[(prevIndex + 1) % keyframes.size]

        val nextTime = if (next.time < prev.time) animation.duration else next.time
        val delta = (animation.time - prev.time) / (nextTime - prev.time)

        property.set(animation.instance, prev.interpolate(next, delta))
    }
}

class Animator(vararg val animations: Animation<*>) : Entity.Component() {
    override suspend fun init() = animations.forEach { it.init(entity) }

    override fun update(delta: Double) = animations.forEach { if (it.running) it.update(delta) }
}