package kengine.objects.gl

import kengine.math.Color
import kengine.math.Vector2f
import kengine.math.Vector2i
import kengine.util.Event
import kengine.util.glBool
import kengine.util.terminateError
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.ByteBuffer
import java.nio.IntBuffer

class Window(size: Vector2i, private val title: String, private val resizable: Boolean = true) : Event.Manager() {
    var id: Long = -1L

    class ResizeEvent(val size: Vector2i) : Event()
    class KeyEvent(val key: Int, val code: Int, val action: Int, val mods: Int) : Event()
    class MouseButtonEvent(val button: Int, val action: Int, val mods: Int) : Event()
    class MouseMoveEvent(val pos: Vector2f) : Event()

    var clearColor = Color.black
        set(c) {
            if (id != -1L)
                glClearColor(c.r, c.g, c.b, c.a)
            field = c
        }

    var size = size; private set
    var mousePosition = Vector2f()

    fun init() {
        if (id != -1L) return

        glfwInit()

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_RESIZABLE, glBool(resizable))

        id = glfwCreateWindow(size.x, size.y, title, NULL, NULL)
        if (id == NULL)
            terminateError("Failed to create GLFW window")
        glfwMakeContextCurrent(id)
        GL.createCapabilities()

        // Audio init
        // TODO: Move to separate file
        val device = alcOpenDevice(null as ByteBuffer?)
        val capabilities = ALC.createCapabilities(device)
        val context = alcCreateContext(device, null as IntBuffer?)
        alcMakeContextCurrent(context)
        AL.createCapabilities(capabilities)

        glViewport(0, 0, size.x, size.y)

        glfwSetWindowSizeCallback(id) { _, w, h ->
            this.size = Vector2i(w, h)
            glViewport(0, 0, w, h)

            notify(ResizeEvent(this.size))
        }

        // Update clear color
        clearColor = clearColor
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        val prevPosX = BufferUtils.createIntBuffer(1)
        val prevPosY = BufferUtils.createIntBuffer(1)
        val prevSizeX = BufferUtils.createIntBuffer(1)
        val prevSizeY = BufferUtils.createIntBuffer(1)
        var fullscreen = false

        glfwSetKeyCallback(id) { _, key, scancode, action, mods ->
            if (action == GLFW_PRESS) when (key) {
                GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(id, true)
                GLFW_KEY_F -> {
                    if (fullscreen) {
                        glfwSetWindowPos(id, prevPosX[0], prevPosY[0])
                        glfwSetWindowSize(id, prevSizeX[0], prevSizeY[0])
                    } else {
                        val monitor = glfwGetPrimaryMonitor()
                        val mode = glfwGetVideoMode(monitor)!!

                        glfwGetWindowPos(id, prevPosX, prevPosY)
                        glfwGetWindowSize(id, prevSizeX, prevSizeY)

                        glfwSetWindowPos(id, 0, 0)
                        glfwSetWindowSize(id, mode.width(), mode.height())
                    }
                    fullscreen = !fullscreen
                }
            }

            notify(KeyEvent(key, scancode, action, mods))
        }

        glfwSetMouseButtonCallback(id) { _, button, action, mods ->
            notify(MouseButtonEvent(button, action, mods))
        }

        glfwSetCursorPosCallback(id) { _, x, y ->
            mousePosition = Vector2f(x.toFloat(), size.y - y.toFloat()) - Vector2f(size) / 2f
            notify(MouseMoveEvent(mousePosition))
        }
    }
}