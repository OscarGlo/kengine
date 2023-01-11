package entity.components.render

import objects.gl.Image
import org.joml.Vector2f
import org.joml.Vector4f
import util.rectIndices
import util.rectVertices
import util.terminateError
import util.white

open class AtlasTexture(
    image: Image,
    uvs: List<Vector4f>,
    color: Vector4f = white
) : ImageRender(
    image,
    genVertices(image, uvs[0]),
    rectIndices,
    color
) {
    companion object {
        fun genVertices(image: Image, uv: Vector4f) = rectVertices(
            (uv.z - uv.x) * image.width,
            (uv.w - uv.y) * image.height,
            Vector2f(),
            uv
        )
    }

    private val verticesCache = uvs.map { genVertices(image, it) }

    val frameCount = uvs.size
    var frame = 0
        set(f) {
            if (f > verticesCache.size)
                terminateError("No uv coordinates for frame $f")
            arrayBuffer.store(verticesCache[f])
            field = f
        }
}