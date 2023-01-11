package entity.components.render

import entity.components.Transform2D
import objects.KEFont
import objects.gl.Shader
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.opengl.GL30.*
import util.rectIndicesN
import util.rectVertices
import util.sizeof
import util.white
import java.awt.font.GlyphMetrics
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

class Text(val font: KEFont, text: String = "", private var color: Vector4f = white) : Render(stringVertices(font, text), rectIndicesN(text.length)) {
    companion object {
        private fun stringVertices(font: KEFont, s: String): FloatArray {
            val tmp = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
            val tmpGfx = tmp.createGraphics()
            tmpGfx.font = font.font

            val vec = font.font.createGlyphVector(tmpGfx.fontRenderContext, s)

            return s.mapIndexed { i, c ->
                characterVertices(font, c, vec.getGlyphMetrics(i), vec.getGlyphPosition(i))
            }.reduce(FloatArray::plus)
        }

        private fun characterVertices(font: KEFont, c: Char, metrics: GlyphMetrics, pos: Point2D) =
            metrics.bounds2D.let {
                rectVertices(
                    it.width.toFloat(),
                    it.height.toFloat(),
                    Vector2f(
                        (pos.x + it.centerX).toFloat(),
                        (pos.y - it.centerY).toFloat()
                    ),
                    font.characterBounds[c.code]!!
                )
            }
    }

    override val shader = Shader(
        GL_VERTEX_SHADER to "/shaders/base.vert",
        GL_FRAGMENT_SHADER to "/shaders/modulateTexture.frag"
    )

    var text = text
        set(t) {
            field = t
            arrayBuffer.store(stringVertices(font, t))
            elementBuffer.store(rectIndicesN(text.length))
        }

    override fun render() {
        shader.use()
        shader["transform"] = entity.get<Transform2D>().global()
        shader["color"] = color

        font.texture.bind()
        vertexArray.bind()
        elementBuffer.bind()

        for (i in text.indices) {
            val n = i * 6
            glDrawRangeElements(GL_TRIANGLES, n, n + 6, 6, GL_UNSIGNED_INT, n.toLong() * sizeof(GL_UNSIGNED_INT))
        }
    }
}