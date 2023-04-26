package kengine.objects.glfw

import kengine.math.Color
import kengine.math.Vector2f
import kengine.math.Vector2i
import kengine.util.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.NULL
import java.nio.ByteBuffer
import java.nio.IntBuffer

fun KeyModifiers.Companion.from(mods: Int) = KeyModifiers(
    mods and 1 != 0,
    mods and 2 != 0,
    mods and 4 != 0,
    mods and 8 != 0,
    mods and 16 != 0,
    mods and 32 != 0
)

fun CursorMode.glfw() = when (this) {
    CursorMode.Normal -> GLFW_CURSOR_NORMAL
    CursorMode.Hidden -> GLFW_CURSOR_HIDDEN
    CursorMode.Locked -> GLFW_CURSOR_DISABLED
}

actual class Window actual constructor(size: Vector2i, private val title: String, private val resizable: Boolean) :
    Event.Manager(), Initable {
    var id: Long = -1L
    override var isInit = false

    override val initableProperties = mutableListOf<Initable.RequiresInit<*>>()

    actual val closed get() = isInit && glfwWindowShouldClose(id)

    actual var size = size; private set
    actual var mousePosition = Vector2f()

    actual var vSync by Initable.RequiresInit(this, false) { glfwSwapInterval(boolInt(it)) }

    actual var clearColor by Initable.RequiresInit(this, Color.black) { glClearColor(it.r, it.g, it.b, it.a) }
    actual var cursor by Initable.RequiresInit(this, Cursor.arrow) { glfwSetCursor(id, it.id) }
    actual var cursorMode by Initable.RequiresInit(this, CursorMode.Normal) {
        glfwSetInputMode(id, GLFW_CURSOR, it.glfw())
    }
    actual var icon by Initable.RequiresInit<GLFWImageWrapper?>(this, null) {
        it?.init()
        glfwSetWindowIcon(id, it?.toBuffer() ?: GLFWImage.malloc(0))
    }

    actual fun init() {
        if (isInit) return

        glfwInit()

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_RESIZABLE, boolInt(resizable))

        id = glfwCreateWindow(size.x, size.y, title, NULL, NULL)
        if (id == NULL) terminateError("Failed to create GLFW window")
        glfwMakeContextCurrent(id)
        GL.createCapabilities()
        isInit = true

        // Audio init
        // TODO: Move to separate file
        val device = alcOpenDevice(null as ByteBuffer?)
        val capabilities = ALC.createCapabilities(device)
        val context = alcCreateContext(device, null as IntBuffer?)
        alcMakeContextCurrent(context)
        AL.createCapabilities(capabilities)

        glViewport(0, 0, size.x, size.y)

        Cursor.cursors.forEach { it.init() }
        initableProperties.forEach { it.init() }

        @Suppress("RemoveRedundantQualifierName")
        GL30.glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glfwSetWindowSizeCallback(id) { _, w, h ->
            size = Vector2i(w, h)
            glViewport(0, 0, w, h)
            notify(ResizeEvent(size))
        }

        glfwSetKeyCallback(id) { _, key, _, action, mods ->
            notify(KeyEvent(Key[key], action != GLFW_RELEASE, action == GLFW_REPEAT, KeyModifiers.from(mods)))
        }

        glfwSetMouseButtonCallback(id) { _, button, action, mods ->
            notify(MouseButtonEvent(button, action == GLFW_PRESS, KeyModifiers.from(mods)))
        }

        glfwSetCursorPosCallback(id) { _, x, y ->
            mousePosition = Vector2f(x.toFloat(), size.y - y.toFloat()) - Vector2f(size) / 2f
            notify(MouseMoveEvent(mousePosition))
        }
    }

    actual fun update() {
        glfwSwapBuffers(id)
        glfwPollEvents()
        glFlush()
    }

    actual fun clear() = glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    actual fun close() = glfwSetWindowShouldClose(id, true)
}