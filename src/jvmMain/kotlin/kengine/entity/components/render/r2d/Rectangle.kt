package kengine.entity.components.render.r2d

import kengine.math.Color
import kengine.math.Vector2f
import kengine.util.rectIndices
import kengine.util.rectVertices

open class Rectangle(size: Vector2f, val color: Color) : Render2D(rectVertices(size), rectIndices) {
    override fun renderSteps() = colored(2, color)
}