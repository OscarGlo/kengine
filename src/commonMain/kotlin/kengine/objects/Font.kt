package kengine.objects

import kengine.math.Color
import kengine.math.Rect
import kengine.objects.gl.GLImage
import kengine.util.Resource
import java.awt.FontMetrics
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.net.URL
import kotlin.math.sqrt
import java.awt.Font as AWTFont

const val V_STRETCH = 1.3f
const val MARGIN = 1

// TODO: Expect texture and metrics
class Font(url: URL, val size: Int) {
    constructor(path: String, size: Int) : this(Resource.local(path), size)

    companion object {
        val cache = mutableMapOf<URL, AWTFont>()
    }

    val font: AWTFont
    val characterBounds = mutableMapOf<Int, Rect>()
    val texture: GLImage

    val metrics: FontMetrics

    init {
        val res = url.openStream()
        font = cache.getOrPut(url) {
            AWTFont.createFont(AWTFont.TRUETYPE_FONT, res)
        }.deriveFont(size.toFloat())

        // Calculate character bounding boxes
        val tmp = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
        val tmpGfx = tmp.createGraphics()
        tmpGfx.font = font

        val printable = (0..font.numGlyphs).count(font::canDisplay)

        metrics = tmpGfx.fontMetrics
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
}