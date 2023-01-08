package demo

import entity.components.Script
import entity.components.render.Text
import entity.components.Transform2D
import objects.Window
import org.joml.Matrix4f
import kotlin.math.round

class FpsCounter(private val window: Window) : Script() {
    lateinit var text: Text
    lateinit var transform: Transform2D

    private val deltas = mutableListOf<Long>()

    override fun init() {
        transform.translate(-window.width / 2f + 5, window.height / 2f - 16)
    }

    override fun update(delta: Long, time: Long) {
        deltas.add(delta)
        if (deltas.size > 30)
            deltas.removeAt(0)

        text.text = "${round(1000 / deltas.average()).toInt()} fps"
    }

    override fun onResize(width: Int, height: Int) {
        transform
            .set(Matrix4f())
            .translate(-window.width / 2f + 5, window.height / 2f - text.font.size)
    }
}