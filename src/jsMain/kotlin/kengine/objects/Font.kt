package kengine.objects

import kengine.math.Rect
import kengine.objects.gl.GLImage
import kengine.util.Resource

actual class Font actual constructor(resource: Resource, size: Int) {
    actual val characterBounds: MutableMap<Int, Rect>
        get() = TODO("Not yet implemented")
    actual val texture: GLImage
        get() = TODO("Not yet implemented")
    actual val metrics: FontMetrics
        get() = TODO("Not yet implemented")

    actual fun stringMetrics(str: String): List<GlyphMetrics> {
        TODO("Not yet implemented")
    }
}