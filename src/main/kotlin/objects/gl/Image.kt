package objects.gl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import util.Resource
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Image(private val image: BufferedImage, bpp: Int = 4, private val filter: Boolean = true) {
    constructor(path: String, bpp: Int = 4, filter: Boolean = true) :
            this(ImageIO.read(Resource.local(path)), bpp, filter)

    val width = image.width
    val height = image.height

    private val buffer = BufferUtils.createByteBuffer(width * height * bpp).also {
        val pixels = IntArray(width * height)
        image.getRGB(0, 0, width, height, pixels, 0, width)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = pixels[y * width + x]
                it.put((pixel shr 16 and 0xFF).toByte())
                it.put((pixel shr 8 and 0xFF).toByte())
                it.put((pixel and 0xFF).toByte())
                it.put((pixel shr 24 and 0xFF).toByte())
            }
        }
        it.flip()
    }

    private val texture = glGenTextures().also {
        glBindTexture(GL_TEXTURE_2D, it)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        val filterType = if (filter) GL_LINEAR else GL_NEAREST
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterType)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterType)

        glTexImage2D(
            GL_TEXTURE_2D, 0,
            GL_RGBA8, width, height, 0,
            GL_RGBA,
            GL_UNSIGNED_BYTE, buffer
        )
    }

    fun bind() = glBindTexture(GL_TEXTURE_2D, texture)

    fun save(path: String) = ImageIO.write(image, "png", File(path))
}