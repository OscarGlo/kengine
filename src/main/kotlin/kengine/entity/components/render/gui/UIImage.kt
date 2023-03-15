package kengine.entity.components.render.gui

import kengine.math.Vector2f
import kengine.objects.gl.Image
import kengine.util.rectIndices
import kengine.util.rectVertices

class UIImage(image: Image, val size: Vector2f) : UINode(rectVertices(size), rectIndices, image) {
    override fun size() = size
    override fun renderSteps() = textured(2, images[0])
}