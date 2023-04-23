package kengine.objects.glfw

expect abstract class Cursor {
    companion object {
        val cursors: MutableList<Cursor>

        val arrow: Cursor
        val ibeam: Cursor
        val crosshair: Cursor
        val hand: Cursor
        val resizeH: Cursor
        val resizeV: Cursor
        val resizeNWSE: Cursor
        val resizeNESW: Cursor
    }

    abstract fun init()
}

// TODO: Add generic custom cursors