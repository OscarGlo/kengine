package kengine.entity.components.render.gui.input

import kengine.entity.components.render.gui.Text
import kengine.entity.components.render.gui.UICustom
import kengine.math.Vector2f
import kengine.objects.KERuntime
import kengine.objects.glfw.Window
import kengine.tools.Event
import kengine.util.rectIndicesN
import kengine.util.rectVertices
import org.lwjgl.glfw.GLFW.*

class Button(size: Vector2f, val text: String) :
    UICustom(size, rectIndicesN(1 + text.length)) {
    companion object {
        var nextId = 0
    }

    class PressedEvent(val button: Button) : Event()
    class ReleasedEvent(val button: Button) : Event()

    val id = nextId++

    override fun calculateVertices() =
        rectVertices(size) + Text.stringVertices(
            theme.font,
            text,
            Vector2f(-size.x / 2 + theme.borderWidth, -size.y / 2)
        )

    var pressed = false; private set

    @Event.Listener
    fun onMouseClick(evt: Window.MouseButtonEvent): Boolean {
        val mouse = KERuntime.window.mousePosition
        if (evt.action == GLFW_PRESS && evt.button == GLFW_MOUSE_BUTTON_LEFT && mouse in bounds()) {
            pressed = true
            notify(PressedEvent(this))
            return false
        } else if (evt.action == GLFW_RELEASE && pressed) {
            pressed = false
            notify(ReleasedEvent(this))
        }
        return true
    }

    override fun renderSteps() {
        colored(2, if (pressed) theme.activeColor else theme.accentColor)
        text(text)
    }
}