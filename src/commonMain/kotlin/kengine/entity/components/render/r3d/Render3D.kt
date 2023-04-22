package kengine.entity.components.render.r3d

import kengine.entity.components.render.Render
import kengine.objects.gl.GLImage
import kengine.objects.gl.Shader
import kengine.objects.gl.VertexAttribute
import kengine.objects.gl.VertexAttributes

abstract class Render3D(
    vertices: FloatArray,
    indices: IntArray,
    vararg images: GLImage
) : Render(vertices, indices, *images) {
    companion object {
        val defaultAttributes = VertexAttributes(VertexAttribute(3), VertexAttribute(3), VertexAttribute(2))

        val phong = Shader(
            Shader.VERTEX to "/shaders/3d/base.vert",
            Shader.FRAGMENT to "/shaders/3d/phong.frag"
        )
    }

    override val defaultAttributes = Companion.defaultAttributes
}