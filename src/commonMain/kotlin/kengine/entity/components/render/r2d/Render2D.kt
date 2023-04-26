package kengine.entity.components.render.r2d

import kengine.entity.components.render.Render
import kengine.math.Color
import kengine.math.Matrix4
import kengine.objects.gl.GLImage
import kengine.objects.gl.Shader
import kengine.objects.gl.VertexAttribute
import kengine.objects.gl.VertexAttributes
import kengine.util.Resource

abstract class Render2D(
    vertices: FloatArray,
    indices: IntArray,
    vararg images: GLImage
) : Render(vertices, indices, *images) {
    companion object {
        val defaultAttributes = VertexAttributes(VertexAttribute(2), VertexAttribute(2))

        val colorShader = Shader(
            Shader.VERTEX to Resource("/shaders/2d/base.vert", false),
            Shader.FRAGMENT to Resource("/shaders/solidColor.frag", false)
        )

        val imageShader = Shader(
            Shader.VERTEX to Resource("/shaders/2d/base.vert", false),
            Shader.FRAGMENT to Resource("/shaders/modulateTexture.frag", false)
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