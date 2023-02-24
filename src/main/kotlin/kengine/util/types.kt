@file:Suppress("UNCHECKED_CAST")

package kengine.util

import kotlin.math.max
import kotlin.math.min
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun <T, V: Any> lazyVar(initFn: () -> V) = object : ReadWriteProperty<T, V> {
    lateinit var v: V

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (!::v.isInitialized)
            v = initFn()
        return v
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        v = value
    }
}

fun <T : Number> Number.to(kClass: KClass<T>) = when (kClass) {
    Double::class -> toDouble()
    Float::class -> toFloat()

    Long::class -> toLong()
    Int::class -> toInt()
    Short::class -> toShort()
    Byte::class -> toByte()

    else -> terminateError("Wrong number conversion to type ${kClass.simpleName}")
} as T

fun <T : Number> numMax(a: T, b: T) = when (a::class) {
    Double::class -> max(a.toDouble(), b.toDouble())
    Float::class -> max(a.toFloat(), b.toFloat())

    Long::class -> max(a.toLong(), b.toLong())
    Int::class, Short::class, Byte::class -> max(a.toInt(), b.toInt())

    else -> terminateError("Wrong number max using type ${a::class.simpleName}")
} as T

fun <T : Number> numMin(a: T, b: T) = when (a::class) {
    Double::class -> min(a.toDouble(), b.toDouble())
    Float::class -> min(a.toFloat(), b.toFloat())

    Long::class -> min(a.toLong(), b.toLong())
    Int::class, Short::class, Byte::class -> min(a.toInt(), b.toInt())

    else -> terminateError("Wrong number min using type ${a::class.simpleName}")
} as T

inline fun <reified T : Number> Number.to() = to(T::class)

fun <T : Number> T.numPlus(other: Number) = when (this::class) {
    Double::class -> toDouble() + other.toDouble()
    Float::class -> toFloat() + other.toFloat()

    Long::class -> toLong() + other.toLong()
    Int::class -> toInt() + other.toInt()
    Short::class -> toShort() + other.toShort()
    Byte::class -> toByte() + other.toByte()

    else -> terminateError("Wrong number addition to type ${this::class.simpleName}")
} as T

fun <T : Number> T.numMinus(other: Number) = when (this::class) {
    Double::class -> toDouble() - other.toDouble()
    Float::class -> toFloat() - other.toFloat()

    Long::class -> toLong() - other.toLong()
    Int::class -> toInt() - other.toInt()
    Short::class -> toShort() - other.toShort()
    Byte::class -> toByte() - other.toByte()

    else -> terminateError("Wrong number subtraction to type ${this::class.simpleName}")
} as T

fun <T : Number> T.numTimes(other: Number) = when (this::class) {
    Double::class -> toDouble() * other.toDouble()
    Float::class -> toFloat() * other.toFloat()

    Long::class -> toLong() * other.toLong()
    Int::class -> toInt() * other.toInt()
    Short::class -> toShort() * other.toShort()
    Byte::class -> toByte() * other.toByte()

    else -> terminateError("Wrong number multiplication to type ${this::class.simpleName}")
} as T

fun <T : Number> T.numDiv(other: Number) = when (this::class) {
    Double::class -> toDouble() / other.toDouble()
    Float::class -> toFloat() / other.toFloat()

    Long::class -> toLong() / other.toLong()
    Int::class -> toInt() / other.toInt()
    Short::class -> toShort() / other.toShort()
    Byte::class -> toByte() / other.toByte()

    else -> terminateError("Wrong number division to type ${this::class.simpleName}")
} as T

fun Collection<*>.array() = Array<Any>(size) { 0 }.also {
    this.forEachIndexed { i, e -> if (e != null) it[i] = e }
}