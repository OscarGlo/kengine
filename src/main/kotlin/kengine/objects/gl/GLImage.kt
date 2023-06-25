package kengine.objects.gl

import kengine.objects.Image
import kengine.tools.Resource
import org.lwjgl.opengl.GL30.*
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import kotlin.properties.Delegates

class GLImage(image: BufferedImage, bpp: Int = 4, val filter: Boolean = true) : Image(image, bpp) {
    constructor(url: URL, bpp: Int = 4, filter: Boolean = true) : this(ImageIO.read(url), bpp, filter)
    constructor(path: String, bpp: Int = 4, filter: Boolean = true) : this(Resource.local(path), bpp, filter)

    private var id by Delegates.notNull<Int>()

    override fun init() {
        if (isInit) return

        isInit = true
        id = glGenTextures().also {
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

    fun bind() = glBindTexture(GL_TEXTURE_2D, id)

    fun save(path: String) = ImageIO.write(image, "png", File(path))
}