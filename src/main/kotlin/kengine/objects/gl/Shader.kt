package kengine.objects.gl

import kengine.math.Four
import kengine.math.Matrix4
import kengine.math.Vector
import kengine.util.Resource
import kengine.util.terminateError
import org.lwjgl.opengl.GL20.*
import kotlin.properties.Delegates

class Shader(private vararg val steps: Pair<Int, String>) {
    companion object {
        val cache = mutableMapOf<String, Int>()
    }

    private var isInit = false
    private var id by Delegates.notNull<Int>()

    fun init() {
        if (isInit) return
        isInit = true

        id = glCreateProgram()

        steps.forEach { (type, path) ->
            val sid = cache.getOrPut(path) {
                glCreateShader(type).also {
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

    operator fun set(name: String, value: Matrix4) = set(name, value) { loc, mat ->
        glUniformMatrix4fv(loc, false, mat.toBuffer())
    }

    operator fun set(name: String, value: Vector<Four, Float, *>) = set(name, value) { loc, vec ->
        glUniform4f(loc, vec[0], vec[1], vec[2], vec[3])
    }
}