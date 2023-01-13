package objects.gl

import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import util.Resource
import util.terminateError

class Shader(vararg steps: Pair<Int, String>) {
    companion object {
        val cache = mutableMapOf<String, Int>()
    }

    private val id = glCreateProgram()

    init {
        println("Program: $id")
        steps.forEach { (type, path) ->
            val sid = cache.getOrPut(path) {
                glCreateShader(type).also {
                    println(it)
                    val source = Resource.global(path).readText()
                    glShaderSource(it, source)
                    glCompileShader(it)
                }
            }

            glAttachShader(id, sid)
            glDeleteShader(sid)
        }
        glLinkProgram(id)

        val success = glGetProgrami(id, GL_LINK_STATUS)
        if (success == GL_FALSE) terminateError("Shader program linking error ${glGetProgramInfoLog(id)}")
    }

    fun use() = this.apply { glUseProgram(id) }

    private fun <T> set(name: String, value: T, fn: (Int, T) -> Unit) = fn(glGetUniformLocation(id, name), value)

    operator fun set(name: String, value: Matrix4f) = set(name, value) { loc, mat ->
        val buf = BufferUtils.createFloatBuffer(16)
        mat.get(buf)
        glUniformMatrix4fv(loc, false, buf)
    }

    operator fun set(name: String, value: Vector4f) = set(name, value) { loc, vec ->
        glUniform4f(loc, vec.x, vec.y, vec.z, vec.w)
    }
}