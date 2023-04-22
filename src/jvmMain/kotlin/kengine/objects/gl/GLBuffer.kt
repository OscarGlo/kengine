package kengine.objects.gl

import kengine.util.terminateError
import org.lwjgl.opengl.GL30.*
import java.nio.*
import kotlin.properties.Delegates

actual class GLBuffer actual constructor(private val target: Int, private val usage: Int) {
    var id by Delegates.notNull<Int>()

    actual fun init() {
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
