package entity.components.render

import org.joml.Vector2f
import org.joml.Vector4f
import util.rectIndices
import util.rectVertices

open class Rect(size: Vector2f, color: Vector4f) :
    ColorRender(color, rectVertices(size), rectIndices)