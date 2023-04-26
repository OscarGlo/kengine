package kengine.objects.gl

import kengine.util.gl
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLRenderingContext as GL

actual class GLBuffer actual constructor(private val target: Int, private val usage: Int) {

    actual companion object {
        actual val STATIC_DRAW = GL.STATIC_DRAW
        actual val DYNAMIC_DRAW = GL.DYNAMIC_DRAW
        actual val STREAM_DRAW = GL.STREAM_DRAW

        actual val ARRAY = GL.ARRAY_BUFFER
        actual val ELEMENT_ARRAY = GL.ELEMENT_ARRAY_BUFFER
    }
    
    lateinit var buffer: WebGLBuffer

    actual fun init() = apply {
        buffer = gl.createBuffer()!!
    }

    actual fun store(bufferData: Any) {
        bind()
        gl.bufferData(target, bufferData as ArrayBuffer, usage)
    }

    actual fun bind() = gl.bindBuffer(target, buffer)
}