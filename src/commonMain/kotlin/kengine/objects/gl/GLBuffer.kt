package kengine.objects.gl

expect class GLBuffer(target: Int, usage: Int) {
    fun init()
    fun store(bufferData: Any)
    fun bind()
}
