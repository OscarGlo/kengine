package kengine.math

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import kengine.entity.components.render.Ellipse
import kengine.objects.Runtime
import kengine.objects.glfw.Window
import kotlin.math.pow

object Bezier {
    private val pascal = listOf(
        listOf(1),
        listOf(1, 1),
        listOf(1, 2, 1),
        listOf(1, 3, 3, 1),
        listOf(1, 4, 6, 4, 1)
    )

    fun <V : Vector<*, Float, V>> interpolate(t: Float, vararg points: V) =
        points.mapIndexed { i, p ->
            p * pascal[points.size - 1][i].toFloat() * t.pow(i) * (1 - t).pow(points.size - 1 - i)
        }.reduce { u, v -> u + v }
}

class Point(pos: Vector2f, color: Color = Color.white) : Entity(
    "Point${n++}",
    Transform2D().apply { matrix.position = Vector3f(pos) },
    Ellipse(Vector2f(5f), 10, color)
) {
    companion object {
        var n = 0
    }
}

fun main() {
    val runtime = Runtime(Window(Vector2i(1000, 600), "KEngine"))

    val points = arrayOf(
        Vector2f(0f, 0f),
        Vector2f(0f, 100f),
        Vector2f(100f, 100f),
        Vector2f(100f, 0f)
    )

    for (i in (1..50))
        runtime.root.add(Point(Bezier.interpolate(i / 50f, *points)))

    for (p in points)
        runtime.root.add(Point(p, Color(1f, 0f, 0f)))

    runtime.run()
}