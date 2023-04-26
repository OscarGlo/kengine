package kengine.objects.gl

import kengine.util.terminateError
import org.lwjgl.opengl.GL30.*
import java.nio.*
import kotlin.properties.Delegates

actual class GLBuffer actual constructor(private val target: Int, private val usage: Int) {
    actual companion object {
        // Target
        actual const val ARRAY = GL_ARRAY_BUFFER
        actual const val ELEMENT_ARRAY = GL_ELEMENT_ARRAY_BUFFER
        const val PIXEL_PACK = GL_PIXEL_PACK_BUFFER
        const val PIXEL_UNPACK = GL_PIXEL_UNPACK_BUFFER
        const val TRANSFORM_FEEDBACK = GL_TRANSFORM_FEEDBACK_BUFFER

        // Usage
        actual const val STATIC_DRAW = GL_STATIC_DRAW
        const val STATIC_READ = GL_STATIC_READ
        const val STATIC_COPY = GL_STATIC_COPY

        actual const val DYNAMIC_DRAW = GL_DYNAMIC_DRAW
        const val DYNAMIC_READ = GL_DYNAMIC_READ
        const val DYNAMIC_COPY = GL_DYNAMIC_COPY

        actual const val STREAM_DRAW = GL_STREAM_DRAW
        const val STREAM_READ = GL_STREAM_READ
        const val STREAM_COPY = GL_STREAM_COPY
    }

    var id by Delegates.notNull<Int>()

    actual fun init() = apply {
        id = glGenBuffers()
    }

    actual fun store(bufferData: Any) {
        bind()
        when (bufferData) {
            is ByteBuffer -> glBufferData(target, bufferData, usage)
            is DoubleBuffer -> glBufferData(target, bufferData, usage)
            is FloatBuffer -> glBufferData(target, bufferData, usage)
            is IntBuffer -> glBufferData(target, bufferData, usage)
            is LongBuffer -> glBufferData(target, bufferData, usage)
            is ShortBuffer -> glBufferData(target, bufferData, usage)
            is DoubleArray -> glBufferData(target, bufferData, usage)
            is FloatArray -> glBufferData(target, bufferData, usage)
            is IntArray -> glBufferData(target, bufferData, usage)
            is Long -> glBufferData(target, bufferData, usage)
            is LongArray -> glBufferData(target, bufferData, usage)
            is ShortArray -> glBufferData(target, bufferData, usage)
            else -> terminateError("Invalid type ${bufferData::class.simpleName} for GLBuffer")
        }
    }

    actual fun bind() = glBindBuffer(target, id)
}
