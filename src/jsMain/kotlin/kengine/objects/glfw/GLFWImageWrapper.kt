package kengine.objects.glfw

import kengine.objects.Image
import kengine.util.Resource

actual class GLFWImageWrapper actual constructor(resource: Resource, bpp: Int) : Image(resource, bpp) {
    actual fun init() {}
}