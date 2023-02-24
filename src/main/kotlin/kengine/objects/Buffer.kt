package kengine.objects

import kotlin.properties.Delegates

abstract class Buffer<T> {
    var id by Delegates.notNull<Int>(); private set

    fun init() = apply {
        id = gen()
    }

    abstract fun gen(): Int
    abstract fun store(bufferData: T)
}