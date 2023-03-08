package kengine.objects.gl

import kengine.math.Vector2i
import kengine.util.Resource
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import kotlin.properties.Delegates

class Image(private val image: BufferedImage, bpp: Int = 4, private val filter: Boolean = true) {
    constructor(url: URL, bpp: Int = 4, filter: Boolean = true) : this(ImageIO.read(url), bpp, filter)
    constructor(path: String, bpp: Int = 4, filter: Boolean = true) : this(Resource.local(path), bpp, filter)

    val size = Vector2i(image.width, image.height)

    private val buffer = BufferUtils.createByteBuffer(size.x * size.y * bpp).also {
        val pixels = IntArray(size.x * size.y)
        image.getRGB(0, 0, size.x, size.y, pixels, 0, size.x)

        for (y in 0 until size.y) {
            for (x in 0 until size.x) {
                val pixel = pixels[y * size.x + x]
                it.put((pixel shr 16 and 0xFF).toByte())
                it.put((pixel shr 8 and 0xFF).toByte())
                it.put((pixel and 0xFF).toByte())
                it.put((pixel shr 24 and 0xFF).toByte())
            }
        }
        it.flip()
    }

    private var texture by Delegates.notNull<Int>()
    private var isInit = false

    fun init() {
        if (isInit) return

        isInit = true
        texture = glGenTextures().also {
            glBindTexture(GL_TEXTURE_2D, it)

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
            val filterType = if (filter) GL_LINEAR else GL_NEAREST
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterType)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterType)

            glTexImage2D(
                GL_TEXTURE_2D, 0,
                GL_RGBA8, size.x, size.y, 0,
                GL_RGBA,
                GL_UNSIGNED_BYTE, buffer
            )
        }
    }

    fun bind() = glBindTexture(GL_TEXTURE_2D, texture)

    fun save(path: String) = ImageIO.write(image, "png", File(path))
}