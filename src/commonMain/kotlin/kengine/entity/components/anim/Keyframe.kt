package kengine.entity.components.anim

import kengine.math.*
import kengine.util.numPlus
import kengine.util.numTimes
import kengine.util.terminateError
import kengine.math.Bezier as MathBezier

open class Keyframe<V : Any>(val value: V, val time: Double, val damping: (Double) -> Double = Shaping.linear) {
    open class Bezier<V : Any>(value: V, time: Double, val before: Tangent<V>, val after: Tangent<V>) :
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
            val beforeNext = if (next is Bezier) next.before else (next.value to 0.0)
            val tangents = arrayOf(
                tangentVector(value to time),
                tangentVector(after, time),
                tangentVector(beforeNext, time),
                tangentVector(next.value to next.time)
            )

            @Suppress("UNCHECKED_CAST")
            return when (value::class) {
                Number::class -> MathBezier.interpolate(delta.toFloat(), *tangents.map { Vector2f(it) }.toTypedArray()).x as V
                Vector2f::class ->
                    Vector2f(MathBezier.interpolate(delta.toFloat(), *tangents.map { Vector3f(it) }.toTypedArray())) as V
                Vector3f::class ->
                    Vector3f(MathBezier.interpolate(delta.toFloat(), *tangents.map { Vector4f(it) }.toTypedArray())) as V
                else -> terminateError("Cannot interpolate value of type ${value::class.simpleName}")
            }
        }
    }

    class SimpleBezier<V : Any>(value: V, time: Double, before: Double, after: Double = before) :
        Bezier<V>(value, time, value to before, value to after)

    open fun interpolate(next: Keyframe<V>, delta: Double): V {
        val d = next.damping(delta)

        if (next is Bezier)
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