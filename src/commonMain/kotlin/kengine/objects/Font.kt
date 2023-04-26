package kengine.objects

import kengine.math.Rect
import kengine.math.Vector2f
import kengine.objects.gl.GLImage
import kengine.util.Resource

const val V_STRETCH = 1.3f
const val MARGIN = 1

class FontMetrics(val descent: Int, val height: Int)

class GlyphMetrics(val bounds: Rect, val position: Vector2f)

expect class Font(resource: Resource, size: Int) {
    val characterBounds: MutableMap<Int, Rect>
    val texture: GLImage

    val metrics: FontMetrics

    fun stringMetrics(str: String) : List<GlyphMetrics>
}