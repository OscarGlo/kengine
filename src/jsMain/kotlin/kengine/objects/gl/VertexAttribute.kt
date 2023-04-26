package kengine.objects.gl

import kengine.util.gl

actual fun VertexAttribute.bind(index: Int, offset: Int, total: Int) {
    gl.vertexAttribPointer(index, size, type, normalized, total, offset)
    gl.enableVertexAttribArray(index)
}