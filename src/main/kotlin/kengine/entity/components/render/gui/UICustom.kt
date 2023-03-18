package kengine.entity.components.render.gui

import kengine.math.Vector2f
import kengine.objects.gl.Image

abstract class UICustom(
    var size: Vector2f,
    indices: IntArray,
    vararg images: Image
) :
    UINode(floatArrayOf(), indices, *images) {
    override fun size() = size

    abstract fun calculateVertices(): FloatArray

    override fun render() {
        arrayBuffer.store(calculateVertices())
        super.render()
    }
}