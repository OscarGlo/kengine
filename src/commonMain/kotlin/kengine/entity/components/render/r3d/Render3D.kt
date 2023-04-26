package kengine.entity.components.render.r3d

import kengine.entity.components.render.Render
import kengine.objects.gl.GLImage
import kengine.objects.gl.Shader
import kengine.objects.gl.VertexAttribute
import kengine.objects.gl.VertexAttributes
import kengine.util.Resource

abstract class Render3D(
    vertices: FloatArray,
    indices: IntArray,
    vararg images: GLImage
) : Render(vertices, indices, *images) {
    companion object {
        val defaultAttributes = VertexAttributes(VertexAttribute(3), VertexAttribute(3), VertexAttribute(2))

        val phong = Shader(
            Shader.VERTEX to Resource("/shaders/3d/base.vert", false),
            Shader.FRAGMENT to Resource("/shaders/3d/phong.frag", false)
        )
    }

    override val defaultAttributes = Companion.defaultAttributes
}