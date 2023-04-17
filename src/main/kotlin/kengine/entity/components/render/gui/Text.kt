package kengine.entity.components.render.gui

import kengine.math.Matrix4
import kengine.math.Vector2f
import kengine.math.Vector3f
import kengine.objects.Font
import kengine.util.rectIndicesN
import kengine.util.rectVertices
import java.awt.font.GlyphMetrics
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.math.max

class Text(text: String = "") : UICustom(Vector2f(), rectIndicesN(text.length)) {
    companion object {
        fun stringVertices(font: Font, s: String, offset: Vector2f = Vector2f()): FloatArray {
            val tmp = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
            val tmpGfx = tmp.createGraphics()
            tmpGfx.font = font.font

            val vec = font.font.createGlyphVector(tmpGfx.fontRenderContext, s)

            return s.mapIndexed { i, c ->
                characterVertices(font, c, vec.getGlyphMetrics(i), vec.getGlyphPosition(i), offset)
            }.fold(floatArrayOf(), FloatArray::plus)
        }

        private fun characterVertices(font: Font, c: Char, metrics: GlyphMetrics, pos: Point2D, offset: Vector2f) =
            metrics.bounds2D.let {
                rectVertices(
                    Vector2f(it.width.toFloat(), it.height.toFloat()),
                    Vector2f(
                        (pos.x + it.centerX + offset.x).toFloat(),
                        (pos.y - it.centerY + font.metrics.descent + offset.y).toFloat()
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

    override fun initialize() {
        super.initialize()
        updateVertices()
    }

    var text = text
        set(t) {
            if (t == field) return
            field = t
            updateVertices()
        }

    private var _width = calculateWidth(vertices)
    override fun size() = Vector2f(_width, theme.font.metrics.height.toFloat())

    override fun calculateVertices() = stringVertices(theme.font, text)

    override fun model() = bounds().run {
        Matrix4(Vector3f(x1, y1, 0f))
    }

    override fun renderSteps() = text(text)
}