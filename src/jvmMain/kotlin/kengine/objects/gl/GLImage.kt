package kengine.objects.gl

import kengine.objects.Image
import kengine.util.Resource
import org.lwjgl.opengl.GL30.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.properties.Delegates

actual class GLImage(bufferedImage: BufferedImage, bpp: Int, val filter: Boolean = true) : Image(bufferedImage, bpp) {
    private var id by Delegates.notNull<Int>()
    actual constructor(resource: Resource, bpp: Int, filter: Boolean) : this(ImageIO.read(resource.url), bpp, filter)

    actual suspend fun init() {
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

    actual fun bind() = glBindTexture(GL_TEXTURE_2D, id)
}