package kengine.entity.components.render

import kengine.math.Color
import kengine.math.Vector2f
import kengine.objects.gl.Image
import kengine.util.rectIndices
import kengine.util.rectVertices

class Texture(image: Image, scale: Vector2f = Vector2f(1f, 1f), color: Color = Color.white) :
    ImageRender(image, rectVertices(Vector2f(image.size) * scale), rectIndices, color) {
    var scale = scale
        set(s) {
            field = s
            arrayBuffer.store(rectVertices(Vector2f(image.size) * scale))
        }
}
