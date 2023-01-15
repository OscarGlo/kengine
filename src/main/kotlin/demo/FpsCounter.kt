package demo

import entity.components.Script
import entity.components.Transform2D
import entity.components.render.Text
import objects.gl.Window
import org.joml.Matrix4f
import util.f
import kotlin.math.round

class FpsCounter(private val window: Window) : Script() {
    lateinit var text: Text
    lateinit var transform: Transform2D

    private val deltas = mutableListOf<Long>()

    override fun init() {
        transform.translate(window.size.f().div(-2f, 2f).add(5f, -16f))
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
            .translate(window.size.f().div(-2f, 2f).add(5f, -16f))
    }
}