package kengine.entity.components

import kengine.entity.Entity
import kengine.math.*
import kengine.util.numPlus
import kengine.util.numTimes
import kengine.util.terminateError
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

class Animator(vararg val animations: Animation<*>) : Entity.Component() {
    open class Keyframe<V : Any>(val value: V, val time: Double, val damping: (Double) -> Double = Shaping.linear) {
        open fun interpolate(next: Keyframe<V>, delta: Double): V {
            val d = next.damping(delta)

            if (next is BezierKeyframe)
                return next.interpolate(this, 1 - d)

            if (value is Number && next.value is Number) {
                val prevPart = value.numTimes(1 - d)
                val nextPart = next.value.numTimes(d)

                return prevPart.numPlus(nextPart)
            } else if (value is Vector<*, *, *> && next.value is Vector<*, *, *>) {
                @Suppress("UNCHECKED_CAST")
                return value.interpolate(next.value, d) as V
            }

            terminateError("Cannot interpolate between ${value::class.simpleName} and ${next.value::class.simpleName}")
        }
    }

    open class BezierKeyframe<V : Any>(value: V, time: Double, val before: Tangent<V>, val after: Tangent<V>) :
        Keyframe<V>(value, time) {
        companion object {
            private fun <V : Any> tangentVector(tangent: Tangent<V>, time: Double = 0.0): Vector<out Size, Float, *> {
                val t = (time + tangent.second).toFloat()
                return when (tangent.first::class) {
                    Number::class -> Vector2f((tangent.first as Number).toFloat(), t)
                    Vector2f::class -> Vector3f(tangent.first as Vector2f, t)
                    Vector3f::class -> Vector4f(tangent.first as Vector3f, t)
                    else -> terminateError("Unexpected tangent value type ${tangent.first::class.simpleName}")
                }
            }
        }

        override fun interpolate(next: Keyframe<V>, delta: Double): V {
            val beforeNext = if (next is BezierKeyframe) next.before else (next.value to 0.0)
            val tangents = arrayOf(
                tangentVector(value to time),
                tangentVector(after, time),
                tangentVector(beforeNext, time),
                tangentVector(next.value to next.time)
            )

            @Suppress("UNCHECKED_CAST")
            return when (value::class) {
                Number::class -> Bezier.interpolate(delta.toFloat(), *tangents.map { Vector2f(it) }.toTypedArray()).x as V
                Vector2f::class ->
                    Vector2f(Bezier.interpolate(delta.toFloat(), *tangents.map { Vector3f(it) }.toTypedArray())) as V
                Vector3f::class ->
                    Vector3f(Bezier.interpolate(delta.toFloat(), *tangents.map { Vector4f(it) }.toTypedArray())) as V
                else -> terminateError("Cannot interpolate value of type ${value::class.simpleName}")
            }
        }
    }

    class SimpleBezierKeyframe<V : Any>(value: V, time: Double, before: Double, after: Double = before) :
        BezierKeyframe<V>(value, time, value to before, value to after)

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

    override fun initialize() = animations.forEach { it.init(entity) }

    override fun update(delta: Double, time: Double) = animations.forEach { if (it.running) it.update(delta) }
}