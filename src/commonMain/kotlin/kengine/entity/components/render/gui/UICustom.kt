package kengine.entity.components.render.gui

import kengine.math.Vector2f
import kengine.objects.gl.GLImage

abstract class UICustom(
    var size: Vector2f,
    indices: IntArray,
    vararg images: GLImage
) :
    UINode(floatArrayOf(), indices, *images) {
    override fun calculateSize() = size

    abstract fun calculateVertices(): FloatArray

    override fun render() {
        arrayBuffer.store(calculateVertices())
        super.render()
    }
}