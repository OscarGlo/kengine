package entity.components.render

import org.joml.Vector2f
import org.joml.Vector4f
import util.ellipseIndices
import util.ellipseVertices

open class Ellipse(size: Vector2f, count: Int, color: Vector4f) :
    ColorRender(color, ellipseVertices(size, count), ellipseIndices(count))