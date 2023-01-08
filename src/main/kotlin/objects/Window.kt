package objects

import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.NULL
import util.bool
import util.terminateError

class Window(width: Int, height: Int, title: String, resizable: Boolean = true) {
    val id: Long

    val resizeListeners = mutableListOf<(Int, Int) -> Unit>()
    val keyListeners = mutableListOf<(Int, Int, Int, Int) -> Unit>()
    val mouseButtonListeners = mutableListOf<(Int, Int, Int) -> Unit>()
    val mouseMoveListeners = mutableListOf<(Double, Double) -> Unit>()

    var clearColor = Vector4f()
        set(c) {
            glClearColor(c.x, c.y, c.z, c.w)
            field = c
        }

    var width = width; private set
    var height = height; private set

    init {
        glfwInit()
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_RESIZABLE, bool(resizable))

        id = glfwCreateWindow(width, height, title, NULL, NULL)
        if (id == NULL)
            terminateError("Failed to create GLFW window")
        glfwMakeContextCurrent(id)
        GL.createCapabilities()

        glViewport(0, 0, width, height)

        glfwSetWindowSizeCallback(id) { _, w, h ->
            this.width = w
            this.height = h
            glViewport(0, 0, w, h)

            resizeListeners.forEach { it(w, h) }
        }

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        val prevPosX = MemoryUtil.memAllocInt(1)
        val prevPosY = MemoryUtil.memAllocInt(1)
        val prevSizeX = MemoryUtil.memAllocInt(1)
        val prevSizeY = MemoryUtil.memAllocInt(1)
        var fullscreen = false

        glfwSetKeyCallback(id) { _, key, scancode, action, mods ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) glfwSetWindowShouldClose(id, true)

            if (key == GLFW_KEY_F && action == GLFW_PRESS) {
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

            keyListeners.forEach { it(key, scancode, action, mods) }
        }

        glfwSetMouseButtonCallback(id) { _, button, action, mods ->
            mouseButtonListeners.forEach { it(button, action, mods) }
        }

        glfwSetCursorPosCallback(id) { _, x, y ->
            mouseMoveListeners.forEach { it(x, y) }
        }
    }
}