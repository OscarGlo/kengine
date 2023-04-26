package kengine.objects

import kengine.math.Rect
import kengine.math.Vector2f
import kengine.objects.gl.GLImage
import kengine.util.Resource
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.sqrt
import java.awt.Font as AWTFont

actual class Font actual constructor(resource: Resource, val size: Int) {
    companion object {
        val cache = mutableMapOf<String, AWTFont>()
    }

    private val font: AWTFont

    actual val characterBounds = mutableMapOf<Int, Rect>()
    actual val texture: GLImage

    actual val metrics: FontMetrics

    init {
        val res = resource.url.openStream()
        font = cache.getOrPut(resource.url.path) {
            AWTFont.createFont(AWTFont.TRUETYPE_FONT, res)
        }.deriveFont(size.toFloat())

        // Calculate character bounding boxes
        val tmp = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val tmpGfx = tmp.createGraphics()
        tmpGfx.font = font

        val printable = (0..font.numGlyphs).count(font::canDisplay)

        val fm = tmpGfx.fontMetrics
        metrics = FontMetrics(fm.descent, fm.height)
        val lineHeight = (metrics.height * V_STRETCH).toInt()

        val minWidth = sqrt(printable.toDouble()) * font.size
        var width = minWidth.toInt()
        var height = lineHeight

        var x = 0
        var y = metrics.height

        val characterPos = mutableMapOf<Int, Pair<Int, Int>>()

        for (i in 0 until font.numGlyphs) {
            if (!font.canDisplay(i)) continue

            characterPos[i] = x to y

            x += fm.charWidth(i) + MARGIN
            if (x > width) width = x
            if (x > minWidth) {
                x = 0
                y += lineHeight
                height += lineHeight
            }
        }

        // Render bitmap
        val bitmap = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val gfx = bitmap.createGraphics()
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        gfx.font = font
        gfx.color = Color.WHITE

        characterPos.forEach { (i, p) ->
            // Draw character to bitmap
            gfx.drawString(i.toChar().toString(), p.first, p.second)

            val bounds =
                font.createGlyphVector(gfx.fontRenderContext, Char(i).toString()).getGlyphVisualBounds(0).bounds2D

            // Constrain character positions
            characterBounds[i] = Rect(
                (p.first + bounds.minX - 0.5).toFloat() / width,
                (p.second + bounds.minY - 0.5).toFloat() / height,
                (p.first + bounds.maxX + 0.5).toFloat() / width,
                (p.second + bounds.maxY + 0.5).toFloat() / height,
            )
        }
        gfx.dispose()

        texture = GLImage(bitmap, 4)
    }

    actual fun stringMetrics(str: String): List<GlyphMetrics> {
        val tmp = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val tmpGfx = tmp.createGraphics()
        tmpGfx.font = font

        val vec = font.createGlyphVector(tmpGfx.fontRenderContext, str)

        return str.indices.map { i ->
            val bounds = vec.getGlyphMetrics(i).bounds2D
            val pos = vec.getGlyphPosition(i)
            GlyphMetrics(
                Rect(bounds.minX.toFloat(), bounds.minY.toFloat(), bounds.maxX.toFloat(), bounds.maxY.toFloat()),
                Vector2f(pos.x.toFloat(), pos.y.toFloat())
            )
        }
    }
}