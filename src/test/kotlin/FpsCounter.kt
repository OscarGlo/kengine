import kengine.entity.components.Script
import kengine.entity.components.Transform2D
import kengine.entity.components.render.image.Text
import kengine.math.Vector2f
import kengine.math.Vector3f
import kengine.objects.gl.Window
import kengine.util.Event
import kengine.util.Resource

class FpsCounter(private val window: Window) : Script() {
    lateinit var text: Text
    lateinit var transform: Transform2D

    private val deltas = mutableListOf<Double>()

    private fun anchor() {
        transform.matrix.position = Vector3f(Vector2f(window.size) / Vector2f(-2f, 2f) + Vector2f(5f, -16f))
    }

    override fun init() {
        anchor()
    }

    override fun update(delta: Double, time: Double) {
        deltas.add(delta)
        if (deltas.size > 30)
            deltas.removeAt(0)

        text.text = Resource.getString("fps").format(1 / deltas.average())
    }

    @Event.Listener(Window.ResizeEvent::class)
    fun onResize(evt: Window.ResizeEvent) = anchor()
}