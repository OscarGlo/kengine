package kengine.objects.gl

actual fun VertexAttribute.bind(index: Int, offset: Int, total: Int) {
    gl.vertexAttribPointer(index, size, type, normalized, total, offset)
    gl.enableVertexAttribArray(index)
}