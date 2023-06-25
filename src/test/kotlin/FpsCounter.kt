import kengine.entity.components.Script
import kengine.entity.components.render.gui.Text
import kengine.tools.Resource

class FpsCounter : Script() {
    lateinit var text: Text

    private val deltas = mutableListOf<Double>()

    override fun update(delta: Double) {
        deltas.add(delta)
        if (deltas.size > 30)
            deltas.removeAt(0)

        text.text = Resource.getString("fps", "%.0f fps").format(1 / deltas.average())
    }
}