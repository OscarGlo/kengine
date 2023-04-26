package kengine.entity.components.render.gui

import kengine.math.Vector2f
import kengine.objects.gl.GLImage
import kengine.util.rectIndices
import kengine.util.rectVertices

class UIImage(image: GLImage, val size: Vector2f) : UINode(rectVertices(size), rectIndices, image) {
    override fun calculateSize() = size
    override fun renderSteps() = textured(2, images[0])
}