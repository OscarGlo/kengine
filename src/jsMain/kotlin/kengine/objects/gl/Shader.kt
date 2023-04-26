package kengine.objects.gl

import kengine.math.*
import kengine.util.Resource
import kengine.util.gl
import kengine.util.terminateError
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLShader
import org.khronos.webgl.WebGLRenderingContext as GL

actual class Shader actual constructor(vararg val steps: Pair<Int, Resource>) {
    actual companion object {
        val cache = mutableMapOf<Resource, WebGLShader>()
        val instances = mutableListOf<Shader>()

        actual suspend fun init() = instances.forEach { it.init() }

        actual val VERTEX = GL.VERTEX_SHADER
        actual val FRAGMENT = GL.FRAGMENT_SHADER
    }

    lateinit var program: WebGLProgram

    actual suspend fun init() {
        if (this::program.isInitialized) return

        program = gl.createProgram()!!

        steps.forEach { (type, res) ->
            val sid = cache.getOrPut(res) {
                gl.createShader(type)!!.also {
                    suspend {
                        val source = res.getText()
                        gl.shaderSource(it, source)
                        gl.compileShader(it)
                    }
                }
            }

            gl.attachShader(program, sid)
            gl.deleteShader(sid)
        }
        gl.linkProgram(program)

        val success = gl.getProgramParameter(program, GL.LINK_STATUS)
        if (success == 0) terminateError("Shader program linking error ${gl.getProgramInfoLog(program)}")
    }

    actual fun use() = gl.useProgram(program)

    @Suppress("UNCHECKED_CAST")
    actual operator fun set(name: String, value: Any) {
        val loc = gl.getUniformLocation(program, name)
        when (value::class) {
            Float::class -> gl.uniform1f(loc, value as Float)
            Vector3f::class -> (value as Vector3f).let {
                gl.uniform3f(loc, it[0], it[1], it[2])
            }
            Vector4f::class, Color::class -> (value as Vector<Four, Float, *>).let {
                gl.uniform4f(loc, it[0], it[1], it[2], it[3])
            }
            Matrix4::class -> gl.uniformMatrix4fv(loc, false, (value as Matrix4).values.toTypedArray())
            else -> terminateError("Invalid object class ${value::class.simpleName} for shader parameter")
        }
    }
}