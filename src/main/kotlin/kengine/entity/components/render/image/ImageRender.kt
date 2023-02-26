package kengine.entity.components.render.image

import kengine.entity.components.render.ColorRender
import kengine.math.Color
import kengine.objects.gl.Image
import kengine.objects.gl.Shader
import org.lwjgl.opengl.GL30.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL30.GL_VERTEX_SHADER

abstract class ImageRender(
    protected val image: Image, vertices: FloatArray, indices: IntArray, color: Color = Color.white
) : ColorRender(color, vertices, indices) {
    companion object {
        val shader = Shader(
            GL_VERTEX_SHADER to "/shaders/base.vert",
            GL_FRAGMENT_SHADER to "/shaders/modulateTexture.frag"
        )
    }

    override fun initialize() {
        super.initialize()
        image.init()
    }

    override fun getShader() = shader

    override fun renderBind() {
        super.renderBind()
        image.bind()
    }
}