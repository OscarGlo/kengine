import kengine.entity.components.Script
import kengine.entity.components.render.gui.Text
import kengine.util.Resource

class FpsCounter : Script() {
    lateinit var text: Text

    private val deltas = mutableListOf<Double>()

    override fun update(delta: Double, time: Double) {
        deltas.add(delta)
        if (deltas.size > 30)
            deltas.removeAt(0)

        text.text = Resource.getString("fps").format(1 / deltas.average())
    }
}