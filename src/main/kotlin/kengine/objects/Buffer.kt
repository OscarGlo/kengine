package kengine.objects

abstract class Buffer<T> {
    val id = gen()
    abstract fun gen(): Int
    abstract fun store(bufferData: T)
}