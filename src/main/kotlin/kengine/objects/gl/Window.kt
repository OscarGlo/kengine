package kengine.objects.gl

import org.joml.Vector2i
import org.joml.Vector4f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.NULL
import kengine.objects.util.glBool
import kengine.objects.util.terminateError
import java.nio.ByteBuffer
import java.nio.IntBuffer

class Window(size: Vector2i, title: String, resizable: Boolean = true) {
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

    var size = size; private set

    init {
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

            resizeListeners.forEach { it(w, h) }
        }

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        val prevPosX = BufferUtils.createIntBuffer(1)
        val prevPosY = BufferUtils.createIntBuffer(1)
        val prevSizeX = BufferUtils.createIntBuffer(1)
        val prevSizeY = BufferUtils.createIntBuffer(1)
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