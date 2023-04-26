package kengine.entity.components.render.gui

import kengine.math.Matrix4
import kengine.math.Vector2f
import kengine.math.Vector3f
import kengine.objects.Font
import kengine.objects.GlyphMetrics
import kengine.util.rectIndicesN
import kengine.util.rectVertices
import kotlin.math.max

class Text(text: String = "") : UICustom(Vector2f(), rectIndicesN(text.length)) {
    companion object {
        fun stringVertices(font: Font, s: String, offset: Vector2f = Vector2f()): FloatArray {
            val metrics = font.stringMetrics(s)

            return s.mapIndexed { i, c ->
                characterVertices(font, c, metrics[i], offset)
            }.fold(floatArrayOf(), FloatArray::plus)
        }

        private fun characterVertices(font: Font, c: Char, metrics: GlyphMetrics, offset: Vector2f) =
            metrics.bounds.let {
                rectVertices(
                    it.size,
                    Vector2f(
                        (metrics.position.x + it.center.x + offset.x),
                        (metrics.position.y - it.center.y + font.metrics.descent + offset.y)
                    ),
                    font.characterBounds[c.code]!!
                )
            }
    }

    private fun calculateWidth(vertices: FloatArray) = vertices
        .asIterable()
        .chunked(4)
        .fold(-Float.MAX_VALUE) { m, vertex -> max(m, vertex[0]) }

    fun updateVertices() {
        val vertices = calculateVertices()
        arrayBuffer.store(vertices)
        elementBuffer.store(rectIndicesN(text.length))

        _width = calculateWidth(vertices)
    }

    override suspend fun init() {
        super.init()
        updateVertices()
    }

    var text = text
        set(t) {
            if (t == field) return
            field = t
            updateVertices()
        }

    private var _width = calculateWidth(vertices)
    override fun calculateSize() = Vector2f(_width, theme.font.metrics.height.toFloat())

    override fun calculateVertices() = stringVertices(theme.font, text)

    override fun model() = bounds().run {
        Matrix4(Vector3f(x1, y1, 0f))
    }

    override fun renderSteps() = text(text)
}