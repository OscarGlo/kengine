package kengine.entity.components.render

import kengine.util.ellipseIndices
import kengine.util.ellipseVertices
import org.joml.Vector2f
import org.joml.Vector4f

open class Ellipse(size: Vector2f, count: Int, color: Vector4f) :
    ColorRender(color, ellipseVertices(size, count), ellipseIndices(count))