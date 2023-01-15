package entity

import entity.components.Transform2D
import objects.gl.Window
import org.joml.Matrix4f
import util.times

class Root2D(window: Window) : Entity("") {
    class RootTransform(val viewMatrix: Matrix4f) : Transform2D() {
        var cameraTransform: Matrix4f? = null

        override fun viewport() =
            if (cameraTransform != null)
                cameraTransform!! * viewMatrix
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