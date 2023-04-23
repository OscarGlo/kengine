package kengine.objects.glfw

import kengine.math.Vector2i
import kengine.util.Resource
import kengine.util.terminateError
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryUtil

actual abstract class Cursor {
    actual companion object {
        actual val cursors = mutableListOf<Cursor>()

        actual val arrow: Cursor = StandardCursor(GLFW.GLFW_ARROW_CURSOR)
        actual val ibeam: Cursor = StandardCursor(GLFW.GLFW_IBEAM_CURSOR)
        actual val crosshair: Cursor = StandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR)
        actual val hand: Cursor = StandardCursor(GLFW.GLFW_HAND_CURSOR)
        actual val resizeH: Cursor = StandardCursor(GLFW.GLFW_HRESIZE_CURSOR)
        actual val resizeV: Cursor = StandardCursor(GLFW.GLFW_VRESIZE_CURSOR)

        actual val resizeNWSE: Cursor = OSCursor(
            CustomCursor(GLFWImageWrapper(Resource("/cursors/windows/nwse.png", false)), Vector2i(8)),
            CustomCursor(GLFWImageWrapper(Resource("/cursors/mac/nwse.png", false)), Vector2i(6))
        )
        actual val resizeNESW: Cursor = OSCursor(
            CustomCursor(GLFWImageWrapper(Resource("/cursors/windows/nesw.png", false)), Vector2i(8)),
            CustomCursor(GLFWImageWrapper(Resource("/cursors/mac/nesw.png", false)), Vector2i(6))
        )
    }

    var id = -1L; protected set

    init {
        cursors.add(this)
    }

    actual abstract fun init()
}

class StandardCursor(private val shape: Int) : Cursor() {
    override fun init() {
        id = GLFW.glfwCreateStandardCursor(shape)
        if (id == MemoryUtil.NULL) terminateError("Error creating standard cursor with shape $shape")
    }
}

class OSCursor(private val windows: Cursor, private val other: Cursor) : Cursor() {
    private lateinit var cursor: Cursor

    override fun init() {
        cursor = if ("windows" in System.getProperty("os.name").lowercase()) windows else other
        cursor.init()
        id = cursor.id
    }
}

class CustomCursor(private val image: GLFWImageWrapper, private val hotspot: Vector2i = Vector2i()) : Cursor() {
    override fun init() {
        image.init()
        id = GLFW.glfwCreateCursor(image.glfwImage, hotspot.x, hotspot.y)
        if (id == MemoryUtil.NULL) terminateError("Error creating custom cursor")
    }
}