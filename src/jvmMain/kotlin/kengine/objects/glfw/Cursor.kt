package kengine.objects.glfw

import kengine.math.Vector2i
import kengine.util.Resource
import kengine.util.terminateError
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryUtil

abstract class Cursor {
    companion object {
        val cursors = mutableListOf<Cursor>()

        val arrow = StandardCursor(GLFW.GLFW_ARROW_CURSOR)
        val ibeam = StandardCursor(GLFW.GLFW_IBEAM_CURSOR)
        val crosshair = StandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR)
        val hand = StandardCursor(GLFW.GLFW_HAND_CURSOR)
        val resizeH = StandardCursor(GLFW.GLFW_HRESIZE_CURSOR)
        val resizeV = StandardCursor(GLFW.GLFW_VRESIZE_CURSOR)

        val resizeNWSE = OSCursor(
            CustomCursor(GLFWImageWrapper(Resource.global("/cursors/windows/nwse.png")), Vector2i(8)),
            CustomCursor(GLFWImageWrapper(Resource.global("/cursors/mac/nwse.png")), Vector2i(6))
        )
        val resizeNESW = OSCursor(
            CustomCursor(GLFWImageWrapper(Resource.global("/cursors/windows/nesw.png")), Vector2i(8)),
            CustomCursor(GLFWImageWrapper(Resource.global("/cursors/mac/nesw.png")), Vector2i(6))
        )
    }

    var id = -1L; protected set

    init {
        cursors.add(this)
    }

    abstract fun init()
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