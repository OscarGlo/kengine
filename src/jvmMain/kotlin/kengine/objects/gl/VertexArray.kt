package kengine.objects.gl

import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glGenVertexArrays
import kotlin.properties.Delegates

actual class VertexArray {
    private var id by Delegates.notNull<Int>()

    actual fun init() = apply {
        id = glGenVertexArrays()
    }

    actual fun bind() = glBindVertexArray(id)
}