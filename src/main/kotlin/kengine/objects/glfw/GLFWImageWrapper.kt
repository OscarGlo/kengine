package kengine.objects.glfw

import kengine.objects.Image
import kengine.util.Resource
import org.lwjgl.glfw.GLFWImage
import java.awt.image.BufferedImage
import java.net.URL
import javax.imageio.ImageIO

class GLFWImageWrapper(image: BufferedImage, bpp: Int = 4) : Image(image, bpp) {
    constructor(url: URL, bpp: Int = 4) : this(ImageIO.read(url), bpp)
    constructor(path: String, bpp: Int = 4) : this(Resource.local(path), bpp)

    lateinit var glfwImage: GLFWImage

    override fun init() {
        if (isInit) return
        isInit = true
        glfwImage = GLFWImage.create().width(size.x).height(size.y).pixels(buffer)
    }

    fun toBuffer(): GLFWImage.Buffer = GLFWImage.malloc(1).put(glfwImage).flip()
}