package kengine.objects.glfw

import kengine.math.Color
import kengine.math.Vector2f
import kengine.math.Vector2i
import kengine.util.Event
import kengine.util.Key

class KeyModifiers(
    val shift: Boolean = false,
    val ctrl: Boolean = false,
    val alt: Boolean = false,
    val superKey: Boolean = false,
    val capsLock: Boolean = false,
    val numLock: Boolean = false
) {
    companion object
}

enum class CursorMode {
    Normal, Hidden, Locked
}

class ResizeEvent(val size: Vector2i) : Event()
class KeyEvent(val key: Key, val pressed: Boolean, val repeat: Boolean, val mods: KeyModifiers) : Event()
// TODO: Wrap mouse buttons codes?
class MouseButtonEvent(val button: Int, val pressed: Boolean, val mods: KeyModifiers) : Event()
class MouseMoveEvent(val position: Vector2f) : Event()

expect class Window(size: Vector2i, title: String, resizable: Boolean = true) : Event.Manager {
    val closed: Boolean

    var size: Vector2i
    var mousePosition: Vector2f
    var vSync: Boolean

    var clearColor: Color
    var cursor: Cursor
    var cursorMode: CursorMode
    var icon: GLFWImageWrapper?

    fun init()
    fun update()
    fun clear()
    fun close()
}

val Window.aspect get() = size.x / size.y.toFloat()