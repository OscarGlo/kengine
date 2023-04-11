package kengine.entity

import kengine.entity.components.Transform
import kengine.entity.components.render.Camera
import kengine.math.Vector3f
import kengine.objects.glfw.Window
import kengine.util.Event

class Root(val window: Window) : Entity("") {
    var currentCamera: Camera? = null

    private val viewTransform = Transform()

    fun view(is3D: Boolean = false, fixed: Boolean = false) =
        if (!fixed && currentCamera != null) {
            if (is3D) currentCamera!!.view() else viewTransform.matrix * currentCamera!!.view()
        } else viewTransform.matrix

    init {
        viewTransform.scale(Vector3f(2f / window.size.x, 2f / window.size.y, 1f))
    }

    @Event.Listener(Window.ResizeEvent::class)
    fun setRootTransform(evt: Window.ResizeEvent) {
        viewTransform.scaling = Vector3f(2f / evt.size.x, 2f / evt.size.y, 1f)
    }

    @Event.Listener(Camera.SetCurrentEvent::class)
    fun onCameraChange(evt: Camera.SetCurrentEvent) {
        currentCamera = evt.camera
    }
}