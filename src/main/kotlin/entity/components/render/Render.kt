package entity.components.render

import entity.Entity
import entity.components.Transform2D
import objects.gl.*
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
    protected val arrayBuffer = GLBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
    protected val elementBuffer = GLBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)

    private val arrayBufferLength = vertices.size

    abstract val shader: Shader

    init {
        arrayBuffer.store(vertices)
        elementBuffer.store(indices)
        vertexAttributes.use()
    }

    open fun renderBind() {
        shader.use()
        shader["transform"] = entity.get<Transform2D>().viewport()

        vertexArray.bind()
        elementBuffer.bind()
    }

    open fun render() {
        renderBind()
        glDrawRangeElements(GL_TRIANGLES, 0, arrayBufferLength, arrayBufferLength, GL_UNSIGNED_INT, 0)
    }
}