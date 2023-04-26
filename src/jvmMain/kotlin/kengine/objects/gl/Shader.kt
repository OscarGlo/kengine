package kengine.objects.gl

import kengine.math.*
import kengine.util.Resource
import kengine.util.terminateError
import org.lwjgl.opengl.GL20.*
import kotlin.properties.Delegates

actual class Shader actual constructor(private vararg val steps: Pair<Int, Resource>) {
    actual companion object {
        val cache = mutableMapOf<Resource, Int>()
        val instances = mutableListOf<Shader>()

        actual suspend fun init() = instances.forEach { it.init() }

        actual val VERTEX = GL_VERTEX_SHADER
        actual val FRAGMENT = GL_FRAGMENT_SHADER
    }

    init {
        instances.add(this)
    }

    private var isInit = false
    private var id by Delegates.notNull<Int>()

    actual suspend fun init() {
        if (isInit) return
        isInit = true

        id = glCreateProgram()

        steps.forEach { (type, res) ->
            val sid = cache.getOrPut(res) {
                glCreateShader(type).also {
                    val source = res.getText()
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

    actual fun use() = glUseProgram(id)

    @Suppress("UNCHECKED_CAST")
    actual operator fun set(name: String, value: Any) {
        val loc = glGetUniformLocation(id, name)
        when (value::class) {
            Float::class -> glUniform1f(loc, value as Float)
            Vector3f::class -> (value as Vector3f).let {
                glUniform3f(loc, it[0], it[1], it[2])
            }
            Vector4f::class, Color::class -> (value as Vector<Four, Float, *>).let {
                glUniform4f(loc, it[0], it[1], it[2], it[3])
            }
            Matrix4::class -> glUniformMatrix4fv(loc, false, (value as Matrix4).values)
            else -> terminateError("Invalid object class ${value::class.simpleName} for shader parameter")
        }
    }
}