package objects.gl

import org.lwjgl.opengl.GL20.*
import util.sizeof

class VertexAttribute(
    val size: Int,
    val type: Int = GL_FLOAT,
    val normalized: Boolean = false
) {
    val sizeBytes = size * sizeof(type)
}

class VertexAttributes(private vararg val attributes: VertexAttribute) {
    fun use() {
        val totalSize = attributes.sumOf { it.sizeBytes }

        var indexCount = 0
        var offset = 0
        attributes.forEach {
            glVertexAttribPointer(indexCount, it.size, it.type, it.normalized, totalSize, offset.toLong())
            glEnableVertexAttribArray(indexCount++)
            offset += it.sizeBytes
        }
    }
}