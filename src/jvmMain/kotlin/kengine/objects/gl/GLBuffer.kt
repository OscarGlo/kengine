package kengine.objects.gl

import kengine.objects.Buffer
import kengine.util.terminateError
import org.lwjgl.opengl.GL30.*
import java.nio.*

class GLBuffer(private val target: Int, private val usage: Int) : Buffer<Any>() {
    override fun gen() = glGenBuffers()

    override fun store(bufferData: Any) {
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

    fun bind() = glBindBuffer(target, id)
}
