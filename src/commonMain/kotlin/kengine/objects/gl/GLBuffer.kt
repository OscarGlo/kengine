package kengine.objects.gl

expect class GLBuffer(target: Int, usage: Int) {
    companion object {
        val STATIC_DRAW: Int
        val DYNAMIC_DRAW: Int
        val STREAM_DRAW: Int

        val ARRAY: Int
        val ELEMENT_ARRAY: Int
    }

    fun init(): GLBuffer
    fun store(bufferData: Any)
    fun bind()
}

