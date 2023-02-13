package kengine.entity.components.render

import kengine.objects.gl.Image
import kengine.objects.gl.Shader
import org.joml.Vector4f
import org.lwjgl.opengl.GL30.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL30.GL_VERTEX_SHADER
import kengine.objects.util.white

open class ImageRender(
    protected val image: Image, vertices: FloatArray, indices: IntArray, color: Vector4f = white
) : ColorRender(color, vertices, indices) {
    companion object {
        val shader = Shader(
            GL_VERTEX_SHADER to "/shaders/base.vert",
            GL_FRAGMENT_SHADER to "/shaders/modulateTexture.frag"
        )
    }

    override val shader = Companion.shader

    override fun renderBind() {
        super.renderBind()
        image.bind()
    }
}