package kengine.entity.components.render

import kengine.objects.Font
import kengine.util.rectIndicesN
import kengine.util.rectVertices
import kengine.util.sizeof
import kengine.util.white
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.opengl.GL30.*
import java.awt.font.GlyphMetrics
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

class Text(val font: Font, text: String = "", color: Vector4f = white) :
    ImageRender(font.texture, stringVertices(font, text), rectIndicesN(text.length), color) {
    companion object {
        private fun stringVertices(font: Font, s: String): FloatArray {
            val tmp = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
            val tmpGfx = tmp.createGraphics()
            tmpGfx.font = font.font

            val vec = font.font.createGlyphVector(tmpGfx.fontRenderContext, s)

            return s.mapIndexed { i, c ->
                characterVertices(font, c, vec.getGlyphMetrics(i), vec.getGlyphPosition(i))
            }.reduce(FloatArray::plus)
        }

        private fun characterVertices(font: Font, c: Char, metrics: GlyphMetrics, pos: Point2D) =
            metrics.bounds2D.let {
                rectVertices(
                    Vector2f(it.width.toFloat(), it.height.toFloat()),
                    Vector2f(
                        (pos.x + it.centerX).toFloat(),
                        (pos.y - it.centerY).toFloat()
                    ),
                    font.characterBounds[c.code]!!
                )
            }
    }

    var text = text
        set(t) {
            field = t
            arrayBuffer.store(stringVertices(font, t))
            elementBuffer.store(rectIndicesN(text.length))
        }

    override fun render() {
        if (!visible) return
        renderBind()
        for (i in text.indices)
            glDrawRangeElements(GL_TRIANGLES, i * 6, i * 6 + 6, 6, GL_UNSIGNED_INT, (i * 6).toLong() * sizeof(GL_UNSIGNED_INT))
    }
}