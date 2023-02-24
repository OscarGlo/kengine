package kengine.objects.gl

import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import kotlin.properties.Delegates

class VertexArray {
    private var id by Delegates.notNull<Int>()

    fun init() = apply {
        id = glGenVertexArrays()
    }

    fun bind() = glBindVertexArray(id)
}