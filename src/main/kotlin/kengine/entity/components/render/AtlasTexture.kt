package kengine.entity.components.render

import kengine.math.Color
import kengine.math.Vector2f
import kengine.math.Vector4f
import kengine.objects.gl.Image
import kengine.util.rectIndices
import kengine.util.rectVertices
import kengine.util.terminateError

open class AtlasTexture(
    image: Image, uvs: List<Vector4f>, color: Color = Color.white
) : ImageRender(
    image, genVertices(image, uvs[0]), rectIndices, color
) {
    companion object {
        fun genVertices(image: Image, uv: Vector4f) = rectVertices(
            Vector2f(uv.z - uv.x, uv.w - uv.y) * Vector2f(image.size), uv = uv
        )
    }

    private val verticesCache = uvs.map { genVertices(image, it) }

    val frameCount = uvs.size
    var frame = 0
        set(f) {
            if (f > verticesCache.size) terminateError("No uv coordinates for frame $f")
            arrayBuffer.store(verticesCache[f])
            field = f
        }
}