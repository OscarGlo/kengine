package kengine.entity.components.render.r3d

import kengine.entity.components.render.Render
import kengine.objects.gl.GLImage
import kengine.objects.gl.Shader
import kengine.objects.gl.VertexAttribute
import kengine.objects.gl.VertexAttributes
import org.lwjgl.opengl.GL30

abstract class Render3D(
    vertices: FloatArray,
    indices: IntArray,
    vararg images: GLImage
) : Render(vertices, indices, *images) {
    companion object {
        val defaultAttributes = VertexAttributes(VertexAttribute(3), VertexAttribute(3), VertexAttribute(2))

        val phong = Shader(
            GL30.GL_VERTEX_SHADER to "/shaders/3d/base.vert",
            GL30.GL_FRAGMENT_SHADER to "/shaders/3d/phong.frag"
        )
    }

    override val defaultAttributes = Companion.defaultAttributes
}