package kengine.entity.components.render.image

import kengine.entity.components.render.Render
import kengine.math.Color
import kengine.math.Vector2f
import kengine.objects.gl.Image
import kengine.util.rectIndices
import kengine.util.rectVertices

class Texture(image: Image, scale: Vector2f = Vector2f(1f, 1f), val color: Color = Color.white) :
    Render(rectVertices(Vector2f(image.size) * scale), rectIndices, image) {
    var scale = scale
        set(s) {
            field = s
            arrayBuffer.store(rectVertices(Vector2f(images[0].size) * scale))
        }

    override fun renderSteps() = textured(2, images[0], color)
}
