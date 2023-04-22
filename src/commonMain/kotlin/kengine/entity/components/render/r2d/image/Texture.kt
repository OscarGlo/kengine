package kengine.entity.components.render.r2d.image

import kengine.entity.components.render.r2d.Render2D
import kengine.math.Color
import kengine.math.Vector2f
import kengine.objects.gl.GLImage
import kengine.util.rectIndices
import kengine.util.rectVertices

class Texture(image: GLImage, scale: Vector2f = Vector2f(1f, 1f), val color: Color = Color.white) :
    Render2D(rectVertices(Vector2f(image.size) * scale), rectIndices, image) {
    var scale = scale
        set(s) {
            field = s
            arrayBuffer.store(rectVertices(Vector2f(images[0].size) * scale))
        }

    override fun renderSteps() = textured(2, images[0], color)
}
