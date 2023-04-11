package kengine.entity.components.render.r2d

import kengine.entity.components.render.Render
import kengine.math.Color
import kengine.math.Matrix4
import kengine.objects.gl.GLImage
import kengine.objects.gl.Shader
import kengine.objects.gl.VertexAttribute
import kengine.objects.gl.VertexAttributes
import org.lwjgl.opengl.GL30.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL30.GL_VERTEX_SHADER

abstract class Render2D(
    vertices: FloatArray,
    indices: IntArray,
    vararg images: GLImage
) : Render(vertices, indices, *images) {
    companion object {
        val defaultAttributes = VertexAttributes(VertexAttribute(2), VertexAttribute(2))

        val colorShader = Shader(
            GL_VERTEX_SHADER to "/shaders/2d/base.vert",
            GL_FRAGMENT_SHADER to "/shaders/solidColor.frag"
        )

        val imageShader = Shader(
            GL_VERTEX_SHADER to "/shaders/2d/base.vert",
            GL_FRAGMENT_SHADER to "/shaders/modulateTexture.frag"
        )
    }

    override val defaultAttributes = Companion.defaultAttributes

    init {
        depthTest = false
    }

    override fun projection() = Matrix4()

    protected fun colored(i: Int, color: Color) {
        bindShader(colorShader, "color" to color)
        triangles(i)
    }

    protected fun textured(i: Int, image: GLImage, color: Color = Color.white) {
        bindShader(imageShader, "color" to color)
        image.bind()
        triangles(i)
    }
}