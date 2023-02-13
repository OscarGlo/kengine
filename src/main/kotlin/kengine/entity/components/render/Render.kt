package kengine.entity.components.render

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import kengine.objects.gl.*
import org.lwjgl.opengl.GL30.*

abstract class Render(
    vertices: FloatArray,
    indices: IntArray
) : Entity.Component() {
    companion object {
        val defaultAttributes = VertexAttributes(VertexAttribute(2), VertexAttribute(2))
    }

    protected val vertexArray = VertexArray()
    protected val arrayBuffer = GLBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
    protected val elementBuffer = GLBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)

    private val arrayBufferLength = vertices.size

    abstract val shader: Shader

    var visible = true

    init {
        arrayBuffer.store(vertices)
        elementBuffer.store(indices)
        defaultAttributes.use()
    }

    open fun renderBind() {
        shader.use()
        shader["transform"] = entity.get<Transform2D>().viewport()

        vertexArray.bind()
        elementBuffer.bind()
    }

    open fun render() {
        if (!visible) return
        renderBind()
        glDrawElements(GL_TRIANGLES, arrayBufferLength, GL_UNSIGNED_INT, 0)
    }
}