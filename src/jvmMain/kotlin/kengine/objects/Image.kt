package kengine.objects

import kengine.math.Vector2i
import kengine.util.Resource
import org.lwjgl.BufferUtils
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import javax.imageio.ImageIO

actual abstract class Image actual constructor(resource: Resource, bpp: Int) {
    protected var isInit = false

    protected val image: BufferedImage = ImageIO.read(resource.url)
    val _size = Vector2i(image.width, image.height)

    protected val buffer: ByteBuffer = BufferUtils.createByteBuffer(size.x * size.y * bpp).also {
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
}