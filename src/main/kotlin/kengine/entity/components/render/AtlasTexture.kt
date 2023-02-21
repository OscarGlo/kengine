package kengine.entity.components.render

import kengine.objects.gl.Image
import kengine.util.*
import org.joml.Vector2f
import org.joml.Vector4f

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
            Vector2f(uv.z - uv.x, uv.w - uv.y) * image.size.f(),
            uv = uv
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