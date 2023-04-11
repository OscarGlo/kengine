package kengine.objects.gl

import kengine.math.*
import kengine.util.Resource
import kengine.util.terminateError
import org.lwjgl.opengl.GL20.*
import kotlin.properties.Delegates

class Shader(private vararg val steps: Pair<Int, String>) {
    companion object {
        val cache = mutableMapOf<String, Int>()
        val instances = mutableListOf<Shader>()

        fun init() = instances.forEach { it.init() }
    }

    init {
        instances.add(this)
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

    @Suppress("UNCHECKED_CAST")
    operator fun set(name: String, value: Any) {
        val loc = glGetUniformLocation(id, name)
        when (value::class) {
            Float::class -> glUniform1f(loc, value as Float)
            Vector3f::class -> (value as Vector3f).let {
                glUniform3f(loc, it[0], it[1], it[2])
            }
            Vector4f::class, Color::class -> (value as Vector<Four, Float, *>).let {
                glUniform4f(loc, it[0], it[1], it[2], it[3])
            }
            Matrix4::class -> glUniformMatrix4fv(loc, false, (value as Matrix4).toBuffer())
            else -> terminateError("Invalid object class ${value::class.simpleName} for shader parameter")
        }
    }
}