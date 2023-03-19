import kengine.entity.Entity
import kengine.entity.components.Script
import kengine.entity.components.Transform2D
import kengine.entity.components.render.Ellipse
import kengine.math.*
import kengine.objects.Runtime
import kengine.objects.gl.Window

class Point(octaves: Int, i: Int, offset: Vector3f, color: Color) : Entity(
    "Point${n++}",
    Transform2D().apply { matrix.position = offset },
    Ellipse(Vector2f(5f), 10, color),
    object : Script() {
        lateinit var transform: Transform2D

        override fun update(delta: Double, time: Double) {
            val noise = Noise.fractal(octaves, 0.5f, Vector2f(i / 100f, time.toFloat() / 5))
            transform.matrix.position = offset + Vector3f(0f, 100 * noise, 0f)
        }
    }
) {
    companion object {
        var n = 0
    }
}

fun main() {
    val runtime = Runtime(Window(Vector2i(1000, 600), "KEngine"))

    for (j in (1..5))
        for (i in (-100..100))
            runtime.root.add(Point(j, i, Vector3f(5f * i, 0f, 0f), Color(j / 5f, 0.5f)))

    runtime.run()
}