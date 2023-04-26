package kengine.objects.glfw

import kengine.math.Color
import kengine.math.Vector2f
import kengine.math.Vector2i
import kengine.util.*
import kotlinx.browser.document
import org.w3c.dom.HTMLLinkElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.khronos.webgl.WebGLRenderingContext as GL
import org.w3c.dom.events.Event as JsEvent

fun KeyModifiers.Companion.get(evt: KeyboardEvent) = KeyModifiers(
    evt.getModifierState("Shift"),
    evt.getModifierState("Control"),
    evt.getModifierState("Alt"),
    evt.getModifierState("OS"),
    evt.getModifierState("CapsLock"),
    evt.getModifierState("NumLock"),
)

actual class Window actual constructor(size: Vector2i, title: String, resizable: Boolean) : Event.Manager() {
    actual var size = Vector2i(canvas.width, canvas.height)
    actual var mousePosition = Vector2f()

    actual var vSync = false
    actual val closed = false

    actual var clearColor = Color.black
        set(c) {
            field = c
            gl.clearColor(c.r, c.g, c.b, c.a)
        }

    actual var cursor = Cursor.arrow
        set(c) {
            field = c
            if (cursorMode == CursorMode.Normal)
                canvas.style.cursor = cursor.css
        }

    actual var cursorMode = CursorMode.Normal
        set(m) {
            if (m != field && field == CursorMode.Locked) {
                val dom: dynamic = document
                val exit = dom.exitPointerLock ?: dom.mozExitPointerLock ?: dom.webkitExitPointerLock
                if (exit != null) exit()
            }
            field = m
            when (m) {
                CursorMode.Normal -> canvas.style.cursor = cursor.css
                CursorMode.Hidden -> canvas.style.cursor = "none"
                CursorMode.Locked -> {
                    val cv: dynamic = canvas
                    val request = cv.requestPointerLock ?: cv.mozRequestPointerLock ?: cv.webkitRequestPointerLock
                    if (request != null) request()
                }
            }
        }

    actual var icon: GLFWImageWrapper? = null
        set(i) {
            field = i
            var link = document.querySelector("link[rel~='icon']") as HTMLLinkElement?
            if (link == null) {
                link = document.createElement("link") as HTMLLinkElement?
                link!!.rel = "icon"
                document.head!!.appendChild(link)
            }
            link.href = i?.resource?.path ?: ""
        }

    actual fun init() {
        gl.viewport(0, 0, size.x, size.y)

        Cursor.cursors.forEach { it.init() }

        gl.enable(GL.BLEND)
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)

        var mods = KeyModifiers()

        fun notifyKey(pressed: Boolean): (JsEvent) -> Unit = {
            val evt = it as KeyboardEvent
            mods = KeyModifiers.get(evt)
            notify(KeyEvent(Key[evt.code], pressed, evt.repeat, mods))
        }

        canvas.addEventListener("keydown", notifyKey(true))
        canvas.addEventListener("keyup", notifyKey(false))

        fun notifyMouseButton(pressed: Boolean): (JsEvent) -> Unit = {
            notify(MouseButtonEvent((it as MouseEvent).button.toInt(), pressed, mods))
        }

        canvas.addEventListener("mousedown", notifyMouseButton(true))
        canvas.addEventListener("mouseup", notifyMouseButton(false))

        canvas.addEventListener("mousemove", { e ->
            val evt = e as MouseEvent
            val rect = canvas.getBoundingClientRect()
            notify(MouseMoveEvent(Vector2f(evt.clientX - rect.left.toFloat(), evt.clientY - rect.top.toFloat())))
        })
    }

    actual fun update() = gl.flush()
    actual fun clear() = gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)
    actual fun close() {}
}