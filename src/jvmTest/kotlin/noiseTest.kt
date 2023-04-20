import kengine.entity.Entity
import kengine.entity.components.Script
import kengine.entity.components.Transform
import kengine.entity.components.render.gui.Text
import kengine.entity.components.render.gui.UINode
import kengine.entity.components.render.r2d.Ellipse
import kengine.math.*
import kengine.objects.KERuntime
import kengine.objects.Scene
import kengine.objects.glfw.Window

class Point(octaves: Int, i: Int, offset: Vector3f, color: Color) : Entity(
    "Point${n++}",
    Transform().translate(offset),
    Ellipse(Vector2f(5f), 10, color),
    object : Script() {
        lateinit var transform: Transform

        override fun update(delta: Double) {
            val noise = Noise.fractal(octaves, 0.5f, Vector2f(i / 100f, entity.time.toFloat() / 5))
            transform.position = offset + Vector3f(0f, 100 * noise, 0f)
        }
    }
) {
    companion object {
        var n = 0
    }
}

fun main() {
    KERuntime.window = Window(Vector2i(1000, 600), "KEngine")

    KERuntime.scene = Scene(
        Entity(
            "fps",
            Text().with(UINode.Position(top = 5f, left = 5f)),
            FpsCounter()
        ),
    )

    for (j in (1..5))
        for (i in (-100..100))
            KERuntime.scene.add(Point(j, i, Vector3f(5f * i, 0f, 0f), Color(j / 5f, 0.5f)))

    KERuntime.run()
}