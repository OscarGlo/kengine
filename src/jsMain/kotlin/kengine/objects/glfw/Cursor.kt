package kengine.objects.glfw

actual abstract class Cursor {
    actual companion object {
        actual val cursors = mutableListOf<Cursor>()

        actual val arrow: Cursor = StandardCursor("default")
        actual val ibeam: Cursor = StandardCursor("text")
        actual val crosshair: Cursor = StandardCursor("crosshair")
        actual val hand: Cursor = StandardCursor("grab")
        actual val resizeH: Cursor = StandardCursor("ns-resize")
        actual val resizeV: Cursor = StandardCursor("ew-resize")
        actual val resizeNWSE: Cursor = StandardCursor("nwse-resize")
        actual val resizeNESW: Cursor = StandardCursor("nesw-resize")
    }

    init {
        cursors.add(this)
    }

    actual abstract fun init()

    abstract val css: String
}

class StandardCursor(name: String) : Cursor() {
    override val css = name

    override fun init() {}
}
