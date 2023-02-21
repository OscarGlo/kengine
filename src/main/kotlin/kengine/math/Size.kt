package kengine.math

import kengine.util.terminateError
import kotlin.reflect.KClass

sealed class Size {
    companion object {
        fun <S : Size> value(kClass: KClass<S>) = when (kClass) {
            Two::class -> 2
            Three::class -> 3
            Four::class -> 4

            else -> terminateError("Unexpected size")
        }
    }
}
object Two : Size()
object Three : Size()
object Four : Size()