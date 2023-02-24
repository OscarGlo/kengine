import kengine.entity.components.Script
import kengine.entity.components.Transform2D
import kengine.entity.components.render.Text
import kengine.math.Vector2f
import kengine.math.Vector3f
import kengine.objects.gl.Window

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

        text.text = "%.0f fps".format(1 / deltas.average())
    }

    override fun onResize(width: Int, height: Int) {
        anchor()
    }
}