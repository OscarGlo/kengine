package kengine.entity.components.render.r2d.image

import kengine.entity.components.render.r2d.Render2D
import kengine.math.Color
import kengine.math.Rect
import kengine.math.Vector2f
import kengine.objects.gl.GLImage
import kengine.util.rectIndices
import kengine.util.rectVertices
import kengine.util.terminateError

open class AtlasTexture(
    image: GLImage, uvs: List<Rect>, val color: Color = Color.white
) : Render2D(
    genVertices(image, uvs[0]), rectIndices, image
) {
    companion object {
        fun genVertices(image: GLImage, uv: Rect) = rectVertices(
            uv.size * Vector2f(image.size), uv = uv
        )
    }

    private val verticesCache = uvs.map { genVertices(images[0], it) }

    val frameCount = uvs.size
    var frame = 0
        set(f) {
            if (f > verticesCache.size) terminateError("No uv coordinates for frame $f")
            arrayBuffer.store(verticesCache[f])
            field = f
        }

    override fun renderSteps() = textured(2, images[0], color)
}