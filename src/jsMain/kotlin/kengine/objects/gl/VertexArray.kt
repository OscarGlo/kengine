package kengine.objects.gl

// TODO: Check that this works
actual class VertexArray {
    var va: dynamic = null

    actual fun init() = apply {
        va = js("gl.genVertexArrays()")
    }

    actual fun bind() = js("gl.bindVertexArray(va)")
}