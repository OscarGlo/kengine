package kengine.objects.glfw

import kengine.objects.Image
import kengine.util.Resource

expect class GLFWImageWrapper(resource: Resource, bpp: Int = 4) : Image {
    fun init()
}