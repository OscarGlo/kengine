package kengine.entity.components.render

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import kengine.objects.gl.*
import org.lwjgl.opengl.GL30.*

abstract class Render(
    private val vertices: FloatArray,
    private val indices: IntArray
) : Entity.Component() {
    companion object {
        val defaultAttributes = VertexAttributes(VertexAttribute(2), VertexAttribute(2))
    }

    protected val vertexArray = VertexArray()
    protected val arrayBuffer = GLBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
    protected val elementBuffer = GLBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)

    private val arrayBufferLength = vertices.size

    abstract fun getShader(): Shader

    var visible = true

    override fun initialize() {
        vertexArray.init().bind()

        arrayBuffer.init().store(vertices)
        elementBuffer.init().store(indices)

        defaultAttributes.use()

        getShader().init()
    }

    open fun renderBind() {
        val shader = getShader()
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