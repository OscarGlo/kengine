package kengine.objects.glfw

import kengine.objects.Image
import kengine.util.Resource
import org.lwjgl.glfw.GLFWImage

actual class GLFWImageWrapper actual constructor(resource: Resource, bpp: Int) : Image(resource, bpp) {
    lateinit var glfwImage: GLFWImage

    actual fun init() {
        if (isInit) return
        isInit = true
        glfwImage = GLFWImage.create().width(size.x).height(size.y).pixels(buffer)
    }

    fun toBuffer(): GLFWImage.Buffer = GLFWImage.malloc(1).put(glfwImage).flip()
}