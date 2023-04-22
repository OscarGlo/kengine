package kengine.objects.gl

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.WebGLBuffer

actual class GLBuffer actual constructor(private val target: Int, private val usage: Int) {
    lateinit var buffer: WebGLBuffer

    actual fun init() {
        buffer = gl.createBuffer()!!
    }

    actual fun store(bufferData: Any) {
        bind()
        gl.bufferData(target, bufferData as ArrayBuffer, usage)
    }

    actual fun bind() = gl.bindBuffer(target, buffer)
}