package entity.components.render

import entity.Entity
import entity.components.Transform2D
import objects.*
import org.lwjgl.opengl.GL30.*

abstract class Render(
    vertices: FloatArray,
    indices: IntArray,
    vertexAttributes: VertexAttributes = defaultAttributes
) : Entity.Component() {
    companion object {
        val defaultAttributes = VertexAttributes(VertexAttribute(2), VertexAttribute(2))
    }

    protected val vertexArray = VertexArray()
    protected val arrayBuffer = Buffer(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    protected val elementBuffer = Buffer(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

    private val arrayBufferLength = vertices.size

    abstract val shader: Shader

    init {
        vertexAttributes.use()
    }

    open fun renderBind() {
        shader.use()
        shader["transform"] = entity.get<Transform2D>().global()

        vertexArray.bind()
        elementBuffer.bind()
    }

    open fun render() {
        renderBind()
        glDrawRangeElements(GL_TRIANGLES, 0, arrayBufferLength, arrayBufferLength, GL_UNSIGNED_INT, 0)
    }
}