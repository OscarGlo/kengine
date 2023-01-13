package entity.components.render

import objects.gl.Shader
import org.joml.Vector4f
import org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL20.GL_VERTEX_SHADER
import util.rectIndices
import util.rectVertices

open class Rect(width: Int, height: Int, private val color: Vector4f) :
    Render(rectVertices(width.toFloat(), height.toFloat()), rectIndices) {
    companion object {
        val shader = Shader(
            GL_VERTEX_SHADER to "/shaders/base.vert",
            GL_FRAGMENT_SHADER to "/shaders/solidColor.frag"
        )
    }

    override val shader = Rect.shader

    override fun renderBind() {
        super.renderBind()
        shader["color"] = color
    }
}