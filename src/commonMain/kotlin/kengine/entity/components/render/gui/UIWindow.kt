package kengine.entity.components.render.gui

import kengine.entity.components.render.gui.Text.Companion.stringVertices
import kengine.math.Rect
import kengine.math.Vector2f
import kengine.objects.KERuntime
import kengine.objects.glfw.MouseButtonEvent
import kengine.objects.glfw.MouseMoveEvent
import kengine.util.rectIndicesN
import kengine.util.rectVertices

class UIWindow(size: Vector2f, var title: String = "", var draggable: Boolean = true, var constrained: Boolean = true) :
    UICustom(size, rectIndicesN(5 + title.length)) {
    private var offset = Vector2f()

    override suspend fun init() {
        super.init()
        listener(this::onMouseButton)
        listener(this::onMouseMove)
    }

    override fun bounds() = super.bounds() + Rect(offset)

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

    private var dragging = false
    private var prevMouse = Vector2f()

    fun onMouseButton(evt: MouseButtonEvent) {
        if (!draggable) return

        if (evt.button == 0) {
            val topbar = bounds().apply { y1 = y2 - theme.topbarHeight }
            if (!evt.pressed) {
                dragging = false
            } else if (KERuntime.window.mousePosition in topbar) {
                dragging = true
                prevMouse = KERuntime.window.mousePosition
            }
        }
    }

    private fun constrain() {
        val bounds = bounds()
        val parent = parentBounds()

        if (bounds.x1 < parent.x1) offset.x += parent.x1 - bounds.x1
        if (bounds.y1 < parent.y1) offset.y += parent.y1 - bounds.y1
        if (bounds.x2 > parent.x2) offset.x -= bounds.x2 - parent.x2
        if (bounds.y2 > parent.y2) offset.y -= bounds.y2 - parent.y2
    }

    fun onMouseMove(evt: MouseMoveEvent) {
        if (dragging) {
            offset += evt.position - prevMouse
            prevMouse = evt.position

            if (constrained) constrain()
        }
    }
}