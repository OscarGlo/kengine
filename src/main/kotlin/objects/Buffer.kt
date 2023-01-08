package objects

import org.lwjgl.opengl.GL30.*
import util.terminateError
import java.nio.*

class Buffer(private val target: Int, bufferData: Any, private val usage: Int) {
    private val id = glGenBuffers()

    init {
        store(bufferData)
    }

    // This is necessary for typechecking to work correctly as Kotlin doesn't have union types
    fun store(bufferData: Any) {
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
            else -> terminateError("Invalid type ${bufferData::class.simpleName} for buffer")
        }
    }

    fun bind() = glBindBuffer(target, id)
}