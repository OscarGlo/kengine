package kengine.entity.components.render

import kengine.objects.gl.Image
import kengine.objects.util.*
import org.joml.Vector2f
import org.joml.Vector4f

class Texture(image: Image, scale: Vector2f = Vector2f(1f, 1f), color: Vector4f = white) :
    ImageRender(image, rectVertices(image.size.f() * scale), rectIndices, color) {
    var scale = scale
        set(s) {
            field = s
            arrayBuffer.store(rectVertices(image.size.f() * scale))
        }
}
