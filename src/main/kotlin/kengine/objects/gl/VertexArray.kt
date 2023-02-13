package kengine.objects.gl

import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays

class VertexArray {
    private val id = glGenVertexArrays()

    init {
        bind()
    }

    fun bind() = glBindVertexArray(id)
}