package kengine.entity.components.render

import org.joml.Vector2f
import org.joml.Vector4f
import kengine.objects.util.ellipseIndices
import kengine.objects.util.ellipseVertices

open class Ellipse(size: Vector2f, count: Int, color: Vector4f) :
    ColorRender(color, ellipseVertices(size, count), ellipseIndices(count))