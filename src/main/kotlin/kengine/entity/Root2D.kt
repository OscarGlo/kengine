package kengine.entity

import kengine.entity.components.Transform2D
import kengine.objects.gl.Window
import org.joml.Matrix4f
import kengine.objects.util.roundTransform
import kengine.objects.util.times

class Root2D(window: Window) : Entity("") {
    class RootTransform(val viewMatrix: Matrix4f) : Transform2D() {
        var cameraTransform: Matrix4f? = null

        override fun rootViewport(fixed: Boolean) = if (!fixed && cameraTransform != null)
            viewMatrix * cameraTransform!!.roundTransform()
        else viewMatrix
    }

    val transform = RootTransform(
        Matrix4f()
            .scale(2f / window.size.x, 2f / window.size.y.toFloat(), 1f)
            .translate(-1f, -1f, 0f)
    )

    init {
        add(transform)
        window.resizeListeners.add(::setRootTransform)
    }

    private fun setRootTransform(width: Int, height: Int) {
        transform.viewMatrix.scaling(2f / width, 2f / height, 0f)
    }
}