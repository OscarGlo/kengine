package kengine.objects.gl

import kengine.util.GL_FLOAT
import kengine.util.sizeof

class VertexAttribute(
    val size: Int,
    val type: Int = GL_FLOAT,
    val normalized: Boolean = false
) {
    val sizeBytes = size * sizeof(type)
}

expect fun VertexAttribute.bind(index: Int, offset: Int, total: Int)

class VertexAttributes(private vararg val attributes: VertexAttribute) {
    fun use() {
        val total = attributes.sumOf { it.sizeBytes }

        var index = 0
        var offset = 0
        attributes.forEach {
            it.bind(index++, offset, total)
            offset += it.sizeBytes
        }
    }
}