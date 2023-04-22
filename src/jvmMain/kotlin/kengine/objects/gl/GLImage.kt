package kengine.objects.gl

import kengine.objects.Image
import kengine.util.Resource
import org.lwjgl.opengl.GL30.*
import kotlin.properties.Delegates

actual class GLImage actual constructor(resource: Resource, bpp: Int, val filter: Boolean) : Image(resource, bpp) {
    private var id by Delegates.notNull<Int>()

    actual val size = _size

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