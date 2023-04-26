import kengine.entity.Entity
import kengine.entity.components.render.gui.Text
import kengine.util.Language
import kengine.util.format

class FpsCounter : Entity.Component() {
    lateinit var text: Text

    private val deltas = mutableListOf<Double>()

    override suspend fun init() {
        text = entity.get<Text>()
    }

    override fun update(delta: Double) {
        deltas.add(delta)
        if (deltas.size > 30)
            deltas.removeAt(0)

        text.text = Language.getOrDefault("fps", "%.0f fps").format(1 / deltas.average())
    }
}