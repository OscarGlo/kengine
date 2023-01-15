package entity.components.render

import objects.gl.Shader
import org.joml.Vector4f
import org.lwjgl.opengl.GL20
import util.white

abstract class ColorRender(
    var color: Vector4f = white,
    vertices: FloatArray,
    indices: IntArray,
) : Render(vertices, indices) {
    companion object {
        val shader = Shader(
            GL20.GL_VERTEX_SHADER to "/shaders/base.vert",
            GL20.GL_FRAGMENT_SHADER to "/shaders/solidColor.frag"
        )
    }

    override val shader = ColorRender.shader

    override fun renderBind() {
        super.renderBind()
        shader["color"] = color
    }
}