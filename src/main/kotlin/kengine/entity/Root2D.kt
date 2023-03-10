package kengine.entity

import kengine.entity.components.Camera2D
import kengine.entity.components.Transform2D
import kengine.math.Matrix4
import kengine.math.Vector3f
import kengine.objects.gl.Window
import kengine.util.Event

class Root2D(window: Window) : Entity("") {
    class RootTransform(val viewMatrix: Matrix4) : Transform2D() {
        var currentCamera: Camera2D? = null

        override fun rootViewport(fixed: Boolean) =
            if (!fixed && currentCamera != null) viewMatrix * currentCamera!!.transform()
            else viewMatrix

        @Event.Listener(Window.ResizeEvent::class)
        fun setRootTransform(evt: Window.ResizeEvent) {
            viewMatrix.scaling = Vector3f(2f / evt.size.x, 2f / evt.size.y, 1f)
        }
    }

    val transform = RootTransform(Matrix4().scale(Vector3f(2f / window.size.x, 2f / window.size.y, 1f)))

    init {
        add(transform)
    }

    @Event.Listener(Camera2D.SetCurrentEvent::class)
    fun onCameraChange(evt: Camera2D.SetCurrentEvent) {
        transform.currentCamera = evt.camera
    }
}