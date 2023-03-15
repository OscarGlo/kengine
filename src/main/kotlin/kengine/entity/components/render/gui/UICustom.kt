package kengine.entity.components.render.gui

import kengine.math.Vector2f
import kengine.objects.gl.Image

abstract class UICustom(
    size: Vector2f,
    indices: IntArray,
    vararg images: Image
) :
    UINode(floatArrayOf(), indices, *images) {
    var size = size
        set(s) {
            field = s
            arrayBuffer.store(calculateVertices())
        }

    override fun size() = size

    abstract fun calculateVertices(): FloatArray

    override fun initialize() {
        super.initialize()
        arrayBuffer.store(calculateVertices())
    }
}