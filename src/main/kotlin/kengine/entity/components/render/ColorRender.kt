package kengine.entity.components.render

import kengine.math.Color
import kengine.objects.gl.Shader
import org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL20.GL_VERTEX_SHADER

abstract class ColorRender(
    var color: Color = Color.white,
    vertices: FloatArray,
    indices: IntArray,
) : Render(vertices, indices) {
    companion object {
        val shader = Shader(
            GL_VERTEX_SHADER to "/shaders/base.vert",
            GL_FRAGMENT_SHADER to "/shaders/solidColor.frag"
        )
    }

    override fun getShader() = shader

    override fun renderBind() {
        super.renderBind()
        getShader()["color"] = color
    }
}