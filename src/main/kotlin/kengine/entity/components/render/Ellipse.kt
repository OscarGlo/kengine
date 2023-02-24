package kengine.entity.components.render

import kengine.math.Color
import kengine.math.Vector2f
import kengine.util.ellipseIndices
import kengine.util.ellipseVertices

open class Ellipse(size: Vector2f, count: Int, color: Color) :
    ColorRender(color, ellipseVertices(size, count), ellipseIndices(count))