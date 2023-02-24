package kengine.entity

import kengine.entity.components.Transform2D
import kengine.math.Matrix4
import kengine.math.Vector3f
import kengine.objects.gl.Window

class Root2D(window: Window) : Entity("") {
    class RootTransform(val viewMatrix: Matrix4) : Transform2D() {
        var cameraTransform: Matrix4? = null

        override fun rootViewport(fixed: Boolean) =
            if (!fixed && cameraTransform != null) viewMatrix * cameraTransform!!
            else viewMatrix
    }

    val transform = RootTransform(Matrix4().scale(Vector3f(2f / window.size.x, 2f / window.size.y, 1f)))

    init {
        add(transform)
        window.resizeListeners.add(::setRootTransform)
    }

    private fun setRootTransform(width: Int, height: Int) {
        transform.viewMatrix.apply {
            scaling = Vector3f(2f / width, 2f / height, 1f)
        }
    }
}