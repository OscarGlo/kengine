package entity.components.render

import objects.gl.Image
import org.joml.Vector4f
import util.rectIndices
import util.rectVertices
import util.white

class Texture(image: Image, color: Vector4f = white) :
    ImageRender(image, rectVertices(image.width.toFloat(), image.height.toFloat()), rectIndices, color)