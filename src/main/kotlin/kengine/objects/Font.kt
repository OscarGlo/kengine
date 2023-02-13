package kengine.objects

import kengine.objects.gl.Image
import kengine.objects.util.Resource
import org.joml.Vector4f
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.sqrt
import java.awt.Font as AWTFont

const val V_STRETCH = 1.3f
const val MARGIN = 1

class Font(path: String, val size: Int) {
    companion object {
        val cache = mutableMapOf<String, AWTFont>()
    }

    val font: AWTFont
    val characterBounds = mutableMapOf<Int, Vector4f>()
    val texture: Image

    init {
        val res = Resource.local(path).openStream()
        font = cache.getOrPut(path) {
            AWTFont.createFont(AWTFont.TRUETYPE_FONT, res)
        }.deriveFont(size.toFloat())

        // Calculate character bounding boxes
        val tmp = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val tmpGfx = tmp.createGraphics()
        tmpGfx.font = font

        val printable = (0..font.numGlyphs).count(font::canDisplay)

        val metrics = tmpGfx.fontMetrics
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

            x += metrics.charWidth(i) + MARGIN
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

            val bounds = font.createGlyphVector(gfx.fontRenderContext, Char(i).toString()).getGlyphVisualBounds(0).bounds2D

            // Constrain character positions
            characterBounds[i] = Vector4f(
                (p.first + bounds.minX - 0.5).toFloat() / width,
                (p.second + bounds.minY - 0.5).toFloat() / height,
                (p.first + bounds.maxX + 0.5).toFloat() / width,
                (p.second + bounds.maxY + 0.5).toFloat() / height,
            )
        }
        gfx.dispose()

        texture = Image(bitmap, 4)
    }
}