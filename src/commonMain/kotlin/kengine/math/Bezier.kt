package kengine.math

import kotlin.math.pow

object Bezier {
    private val pascal = listOf(
        listOf(1),
        listOf(1, 1),
        listOf(1, 2, 1),
        listOf(1, 3, 3, 1),
        listOf(1, 4, 6, 4, 1)
    )

    fun <V : Vector<*, Float, V>> interpolate(t: Float, vararg points: V): V =
        points.mapIndexed { i, p ->
            p * pascal[points.size - 1][i].toFloat() * t.pow(i) * (1 - t).pow(points.size - 1 - i)
        }.reduce { u: V, v: V -> u + v }
}