package entity.components.render

import objects.gl.Image
import objects.gl.Shader
import org.joml.Vector4f
import org.lwjgl.opengl.GL30.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL30.GL_VERTEX_SHADER
import util.white

open class ImageRender(
    private val image: Image, vertices: FloatArray, indices: IntArray, private val color: Vector4f = white
) : Render(vertices, indices) {
    companion object {
        val shader = Shader(
            GL_VERTEX_SHADER to "/shaders/base.vert",
            GL_FRAGMENT_SHADER to "/shaders/modulateTexture.frag"
        )
    }

    override val shader = ImageRender.shader

    override fun renderBind() {
        super.renderBind()
        image.bind()
        shader["color"] = color
    }
}