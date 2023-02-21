package kengine.math

import kengine.util.*
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

open class Vector<S : Size, T : Number>(val kClass: KClass<T>, components: List<T>) {
    constructor(kClass: KClass<T>, vararg components: T) : this(kClass, components.toList())

    val components = components.toMutableList()

    class Component<S : Size, T : Number>(private val index: Int) : ReadWriteProperty<Vector<S, T>, T> {
        override fun getValue(thisRef: Vector<S, T>, property: KProperty<*>) = thisRef[index]

        override fun setValue(thisRef: Vector<S, T>, property: KProperty<*>, value: T) {
            thisRef[index] = value
        }
    }

    init {
        components.map { it.javaClass }.toSet().let {
            if (it.size > 1)
                terminateError("Mismatched Vector component types ${it.map { it.simpleName }}")
        }
    }

    operator fun get(i: Int) = components.getOrElse(i) { terminateError("Invalid vector index $i") }

    operator fun set(i: Int, t: T) {
        if (i !in 0 until components.size)
            terminateError("Invalid vector index $i")
        components[i] = t
    }

    // IMMUTABLE
    inline fun <reified U : Number> to() = Vector<S, U>(U::class, components.map(Number::to))

    fun copy() = Vector<S, T>(kClass, components)

    fun getOrZero(i: Int) = components.getOrElse(i) { 0.to(kClass) }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified S : Size> resize(): Vector<S, T> {
        return when (S::class) {
            Two::class -> Vector2(kClass, getOrZero(0), getOrZero(1))
            Three::class -> Vector3(kClass, getOrZero(0), getOrZero(1), getOrZero(2))
            Four::class -> Vector4(kClass, getOrZero(0), getOrZero(1), getOrZero(2), getOrZero(3))

            else -> terminateError("Unexpected length on vector resize")
        } as Vector<S, T>
    }

    private fun <U : Number> map(kClass: KClass<U>, fn: (it: T, index: Int) -> U) =
        Vector<S, U>(kClass, components.mapIndexed { i, a -> fn(a, i) }.toMutableList())

    private fun map(fn: (it: T, index: Int) -> T) = map(kClass, fn)

    private fun componentwise(other: Vector<S, T>, fn: (it: T, other: T) -> T) =
        map { a, i -> fn(a, other[i]) }

    operator fun plus(other: Vector<S, T>) = componentwise(other) { a, b -> a.numPlus(kClass, b) }
    operator fun plus(n: T) = map { a, _ -> a.numPlus(kClass, n) }

    operator fun minus(other: Vector<S, T>) = componentwise(other) { a, b -> a.numMinus(kClass, b) }
    operator fun minus(n: T) = map { a, _ -> a.numMinus(kClass, n) }

    operator fun times(other: Vector<S, T>) = componentwise(other) { a, b -> a.numTimes(kClass, b) }
    operator fun times(n: T) = map { a, _ -> a.numTimes(kClass, n) }

    operator fun div(other: Vector<S, T>) = componentwise(other) { a, b -> a.numDiv(kClass, b) }
    operator fun div(n: T) = map { a, _ -> a.numDiv(kClass, n) }

    fun floor() = map { a, _ -> kotlin.math.floor(a.to()).to(kClass) }
    fun ceil() = map { a, _ -> kotlin.math.ceil(a.to()).to(kClass) }
    fun round() = map { a, _ -> kotlin.math.round(a.to()).to(kClass) }

    fun length() = sqrt(components.fold(0.0) { acc, a -> acc + a.to<Double>().pow(2.0) }).to(kClass)
    fun distance(other: Vector<S, T>) = (other - this).length()

    // MUTABLE
    private fun mapAssign(fn: (it: T, index: Int) -> T) = apply {
        components.forEachIndexed { i, a -> components[i] = fn(a, i) }
    }

    private fun componentAssign(other: Vector<S, T>, fn: (it: T, other: T) -> T) = apply {
        mapAssign { a, i -> fn(a, other[i]) }
    }

    fun add(other: Vector<S, T>) = componentAssign(other) { a, b -> a.numPlus(kClass, b) }
    fun add(n: T) = mapAssign { a, _ -> a.numPlus(kClass, n) }

    fun subtract(other: Vector<S, T>) = componentAssign(other) { a, b -> a.numMinus(kClass, b) }
    fun subtract(n: T) = mapAssign { a, _ -> a.numMinus(kClass, n) }

    fun multiply(other: Vector<S, T>) = componentAssign(other) { a, b -> a.numTimes(kClass, b) }
    fun multiply(n: T) = mapAssign { a, _ -> a.numTimes(kClass, n) }

    fun divide(other: Vector<S, T>) = componentAssign(other) { a, b -> a.numDiv(kClass, b) }
    fun divide(n: T) = mapAssign { a, _ -> a.numDiv(kClass, n) }

    override fun toString() = components.joinToString(", ", "(", ")")
}

class Vector2<T : Number>(kClass: KClass<T>, x: T, y: T) : Vector<Two, T>(kClass, x, y) {
    var x by Component(0)
    var y by Component(1)

    companion object {
        inline fun <reified T : Number> new(x: T, y: T = x) = Vector2(T::class, x, y)
    }
}

class Vector3<T : Number>(kClass: KClass<T>, x: T, y: T, z: T) : Vector<Three, T>(kClass, x, y, z) {
    var x by Component(0)
    var y by Component(1)
    var z by Component(2)

    companion object {
        inline fun <reified T : Number> new(x: T, y: T, z: T) = Vector3(T::class, x, y, z)
        inline fun <reified T : Number> new(n: T) = Vector3(T::class, n, n, n)
    }
}

class Vector4<T : Number>(kClass: KClass<T>, x: T, y: T, z: T, w: T) : Vector<Four, T>(kClass, x, y, z, w) {
    var x by Component(0)
    var y by Component(1)
    var z by Component(2)
    var w by Component(3)

    companion object {
        inline fun <reified T : Number> new(x: T, y: T, z: T, w: T) = Vector4(T::class, x, y, z, w)
        inline fun <reified T : Number> new(n: T) = Vector4(T::class, n, n, n, n)
    }
}

class Color<T : Number>(kClass: KClass<T>, r: T, g: T, b: T, a: T) : Vector<Four, T>(kClass, r, g, b, a) {
    var r by Component(0)
    var g by Component(1)
    var b by Component(2)
    var a by Component(3)

    companion object {
        inline fun <reified T : Number> new(r: T, g: T, b: T, a: T = 0.to(T::class)) = Color(T::class, r, g, b, a)
        inline fun <reified T : Number> new(gray: T, a: T = 0.to(T::class)) = Color(T::class, gray, gray, gray, a)

        val black = new(0f)
        val white = new(1f)
    }
}