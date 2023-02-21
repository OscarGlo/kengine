package kengine.entity.components.render

import kengine.objects.gl.Shader
import kengine.util.white
import org.joml.Vector4f
import org.lwjgl.opengl.GL20

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

    override val shader = Companion.shader

    override fun renderBind() {
        super.renderBind()
        shader["color"] = color
    }
}