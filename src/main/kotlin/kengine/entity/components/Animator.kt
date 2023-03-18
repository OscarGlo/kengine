package kengine.entity.components

import kengine.entity.Entity
import kengine.math.Vector
import kengine.util.numPlus
import kengine.util.numTimes
import kotlin.reflect.KMutableProperty1

class Animator(vararg val animations: Animation<*, *>) : Entity.Component() {
    class Keyframe<V>(val time: Double, val value: V)

    class Animation<T, V>(
        val getInstance: Entity.() -> T,
        val property: KMutableProperty1<T, V>,
        val duration: Double,
        val keyframes: List<Keyframe<V>>
    ) {
        fun update(entity: Entity, time: Double) {
            val prevIndex = keyframes.foldIndexed(0) { i, acc, key -> if (key.time <= time % duration) i else acc }
            val prev = keyframes[prevIndex]
            val next = keyframes[(prevIndex + 1) % keyframes.size]

            val nextTime = if (next.time < prev.time) duration else next.time
            val progress = (time % duration - prev.time) / (nextTime - prev.time)

            if (prev.value is Number && next.value is Number) {
                val prevPart = prev.value.numTimes(1 - progress)
                val nextPart = next.value.numTimes(progress)

                property.set(getInstance(entity), prevPart.numPlus(nextPart))
            } else if (prev.value is Vector<*, *, *> && next.value is Vector<*, *, *>) {
                @Suppress("UNCHECKED_CAST")
                property.set(getInstance(entity), prev.value.interpolate(next.value, progress) as V)
            } else {
                property.set(getInstance(entity), prev.value)
            }
        }
    }

    override fun update(delta: Double, time: Double) = animations.forEach { it.update(entity, time) }
}