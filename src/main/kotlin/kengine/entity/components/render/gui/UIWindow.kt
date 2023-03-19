package kengine.entity.components.render.gui

import kengine.entity.components.render.gui.Text.Companion.stringVertices
import kengine.math.Rect
import kengine.math.Vector2f
import kengine.objects.gl.Window
import kengine.util.Event
import kengine.util.rectIndicesN
import kengine.util.rectVertices
import org.lwjgl.glfw.GLFW.*

class UIWindow(size: Vector2f, var title: String = "") :
    UICustom(size, rectIndicesN(5 + title.length)) {
    private var offset = Vector2f()

    override fun bounds(): Rect {
        return super.bounds() + Rect(offset)
    }

    override fun calculateVertices(): FloatArray {
        val bounds = bounds()
        val size = bounds.size
        return rectVertices(size) +
                rectVertices(Vector2f(size.x, theme.topbarHeight), Vector2f(0f, size.y / 2 - theme.topbarHeight / 2)) +
                rectVertices(Vector2f(size.x, theme.borderWidth), Vector2f(0f, -size.y / 2 + theme.borderWidth / 2)) +
                rectVertices(Vector2f(theme.borderWidth, size.y), Vector2f(-size.x / 2 + theme.borderWidth / 2, 0f)) +
                rectVertices(Vector2f(theme.borderWidth, size.y), Vector2f(size.x / 2 - theme.borderWidth / 2, 0f)) +
                stringVertices(theme.font, title, Vector2f(-size.x / 2 + theme.borderWidth, size.y / 2 - theme.topbarHeight))
    }

    override fun renderSteps() {
        colored(2, theme.backgroundColor)
        colored(8, theme.accentColor)
        text(title)
    }

    var dragging = false
    var prevMouse = Vector2f()

    @Event.Listener(Window.MouseButtonEvent::class)
    fun onMouseButton(evt: Window.MouseButtonEvent) {
        val topbar = bounds().apply { y1 = y2 - theme.topbarHeight }
        if (evt.action == GLFW_PRESS && evt.button == GLFW_MOUSE_BUTTON_LEFT && root.window.mousePosition in topbar) {
            dragging = true
            prevMouse = root.window.mousePosition
        } else if (evt.action == GLFW_RELEASE) {
            dragging = false
        }
    }

    @Event.Listener(Window.MouseMoveEvent::class)
    fun onMouseMove(evt: Window.MouseMoveEvent) {
        if (dragging) {
            offset += evt.position - prevMouse
            prevMouse = evt.position
        }
    }
}