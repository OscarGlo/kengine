@file:Suppress("UNCHECKED_CAST")

package kengine.util

import kotlin.reflect.KClass

fun <T : Number> Number.to(kClass: KClass<T>) = when (kClass) {
    Double::class -> toDouble()
    Float::class -> toFloat()

    Long::class -> toLong()
    Int::class -> toInt()
    Short::class -> toShort()
    Byte::class -> toByte()

    else -> terminateError("Wrong number conversion to type ${kClass.simpleName}")
} as T

inline fun <reified T : Number> Number.to() = to(T::class)

fun <T : Number> T.numPlus(kClass: KClass<T>, other: Number) = when (kClass) {
    Double::class -> toDouble() + other.toDouble()
    Float::class -> toFloat() + other.toFloat()

    Long::class -> toLong() + other.toLong()
    Int::class -> toInt() + other.toInt()
    Short::class -> toShort() + other.toShort()
    Byte::class -> toByte() + other.toByte()

    else -> terminateError("Wrong number addition to type ${kClass.simpleName}")
} as T

fun <T : Number> T.numMinus(kClass: KClass<T>, other: Number) = when (kClass) {
    Double::class -> toDouble() - other.toDouble()
    Float::class -> toFloat() - other.toFloat()

    Long::class -> toLong() - other.toLong()
    Int::class -> toInt() - other.toInt()
    Short::class -> toShort() - other.toShort()
    Byte::class -> toByte() - other.toByte()

    else -> terminateError("Wrong number subtraction to type ${kClass.simpleName}")
} as T

fun <T : Number> T.numTimes(kClass: KClass<T>, other: Number) = when (kClass) {
    Double::class -> toDouble() * other.toDouble()
    Float::class -> toFloat() * other.toFloat()

    Long::class -> toLong() * other.toLong()
    Int::class -> toInt() * other.toInt()
    Short::class -> toShort() * other.toShort()
    Byte::class -> toByte() * other.toByte()

    else -> terminateError("Wrong number multiplication to type ${kClass.simpleName}")
} as T

fun <T : Number> T.numDiv(kClass: KClass<T>, other: Number) = when (kClass) {
    Double::class -> toDouble() / other.toDouble()
    Float::class -> toFloat() / other.toFloat()

    Long::class -> toLong() / other.toLong()
    Int::class -> toInt() / other.toInt()
    Short::class -> toShort() / other.toShort()
    Byte::class -> toByte() / other.toByte()

    else -> terminateError("Wrong number division to type ${kClass.simpleName}")
} as T