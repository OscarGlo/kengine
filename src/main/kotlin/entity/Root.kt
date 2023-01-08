package entity

import entity.components.Transform2D
import objects.Window
import org.joml.Matrix4f

class Root(window: Window) : Entity2D("") {
    private val transform = get<Transform2D>()

    init {
        setRootTransform(window.width, window.height)
        window.resizeListeners.add(::setRootTransform)
    }

    private fun setRootTransform(width: Int, height: Int) {
        transform
            .set(Matrix4f())
            .scale(2f / width, 2f / height)
            .translate(-1f, -1f)
    }
}