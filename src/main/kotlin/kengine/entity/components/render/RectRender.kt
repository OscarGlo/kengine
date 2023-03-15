package kengine.entity.components.render

import kengine.math.Color
import kengine.math.Vector2f
import kengine.util.rectIndices
import kengine.util.rectVertices

open class RectRender(size: Vector2f, val color: Color) : Render(rectVertices(size), rectIndices) {
    override fun renderSteps() = colored(2, color)
}