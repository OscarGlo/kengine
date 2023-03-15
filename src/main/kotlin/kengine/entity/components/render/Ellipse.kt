package kengine.entity.components.render

import kengine.math.Color
import kengine.math.Vector2f
import kengine.util.ellipseIndices
import kengine.util.ellipseVertices

open class Ellipse(size: Vector2f, val count: Int, val color: Color) :
    Render(ellipseVertices(size, count), ellipseIndices(count)) {
    override fun renderSteps() = colored(count + 1, color)
}