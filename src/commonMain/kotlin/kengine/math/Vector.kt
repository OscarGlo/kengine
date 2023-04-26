package kengine.math

import kengine.util.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.properties.ReadWriteProperty
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class Vector<S : Size, T : Number, V : Vector<S, T, V>>(
    protected val numClass: KClass<T>, protected val vecClass: KClass<V>, vararg components: Number
) {
    class Component<S : Size, T : Number, V : Vector<S, T, V>>(private val index: Int) :
        ReadWriteProperty<Vector<S, T, V>, T> {
        override fun getValue(thisRef: Vector<S, T, V>, property: KProperty<*>) = thisRef[index]

        override fun setValue(thisRef: Vector<S, T, V>, property: KProperty<*>, value: T) {
            thisRef[index] = value
        }
    }

    @Suppress("UNCHECKED_CAST")
    var components = components.map { it.to(numClass) }.toMutableList()

    init {
        components.map { it::class }.toSet().let {
            if (it.size > 1) terminateError("Mismatched Vector component types ${it.map { it.simpleName }}")
        }
    }

    abstract fun instanciate(values: Array<Number>): V

    operator fun get(i: Int) = components.getOrElse(i) { terminateError("Invalid vector index $i") }

    operator fun set(i: Int, t: T) {
        if (i !in 0 until components.size) terminateError("Invalid vector index $i")
        components[i] = t
    }

    // IMMUTABLE
    fun getOrElse(i: Int, n: Number = 0) = components.getOrElse(i) { n.to(numClass) }

    @Suppress("UNCHECKED_CAST")
    private fun map(fn: (it: Number, index: Int) -> Number) = instanciate(
        components.mapIndexed { i, a -> fn(a, i) }.toTypedArray()
    )

    private fun componentwise(other: Vector<S, *, *>, fn: (it: Number, other: Number) -> Number) = map { a, i -> fn(a, other[i]) }

    operator fun plus(other: Vector<S, *, *>) = componentwise(other) { a, b -> a.numPlus(b) }
    operator fun plus(n: Number) = map { a, _ -> a.numPlus(n) }

    operator fun minus(other: Vector<S, *, *>) = componentwise(other) { a, b -> a.numMinus(b) }
    operator fun minus(n: Number) = map { a, _ -> a.numMinus(n) }

    open operator fun times(other: Vector<S, *, *>) = componentwise(other) { a, b -> a.numTimes(b) }
    operator fun times(n: Number) = map { a, _ -> a.numTimes(n) }

    operator fun div(other: Vector<S, *, *>) = componentwise(other) { a, b -> a.numDiv(b) }
    operator fun div(n: Number) = map { a, _ -> a.numDiv(n) }

    infix fun dot(other: Vector<S, *, *>) = components
        .mapIndexed { i, a -> a.toFloat() * other[i].toFloat() }
        .fold(0f) { acc, a -> acc + a }

    infix fun proj(other: Vector<S, *, *>) = dot(other) / other.length()

    operator fun unaryMinus() = map { a, _ -> 0.to(numClass).numMinus(a) }

    fun inverse() = map { a, _ -> 1.to(numClass).numDiv(a) }

    infix fun max(other: Vector<S, *, *>) = componentwise(other) { a, b -> numMax(a, b) }
    infix fun min(other: Vector<S, *, *>) = componentwise(other) { a, b -> numMin(a, b) }

    fun floor() = map { a, _ -> kotlin.math.floor(a.to()).to(numClass) }
    fun ceil() = map { a, _ -> kotlin.math.ceil(a.to()).to(numClass) }
    fun round() = map { a, _ -> kotlin.math.round(a.to()).to(numClass) }

    fun length() = sqrt(components.fold(0.0) { acc, a -> acc + a.toDouble().pow(2.0) }).toFloat()
    fun normalize() = length().let {
        div(if (it == 0f) 1.to(numClass) else it.to(numClass))
    }

    @Suppress("UNCHECKED_CAST")
    fun interpolate(other: Vector<*, *, *>, percent: Double) =
        this * (1 - percent).to(numClass) + (other * percent.to(numClass)) as V

    @Suppress("UNCHECKED_CAST")
    fun distance(other: Vector<S, *, *>) = (other - this).length()

    // MUTABLE
    private fun mapAssign(fn: (it: Number, index: Int) -> Number) = apply {
        components.forEachIndexed { i, a -> components[i] = fn(a, i).to(numClass) }
    }

    private fun componentAssign(other: Vector<S, *, *>, fn: (it: Number, other: Number) -> Number) = apply {
        mapAssign { a, i -> fn(a, other[i]) }
    }

    fun add(other: Vector<S, *, *>) = componentAssign(other) { a, b -> a.numPlus(b) }
    fun add(n: Number) = mapAssign { a, _ -> a.numPlus(n) }

    fun subtract(other: Vector<S, *, *>) = componentAssign(other) { a, b -> a.numMinus(b) }
    fun subtract(n: Number) = mapAssign { a, _ -> a.numMinus(n) }

    fun multiply(other: Vector<S, *, *>) = componentAssign(other) { a, b -> a.numTimes(b) }
    fun multiply(n: Number) = mapAssign { a, _ -> a.numTimes(n) }

    fun divide(other: Vector<S, *, *>) = componentAssign(other) { a, b -> a.numDiv(b) }
    fun divide(n: Number) = mapAssign { a, _ -> a.numDiv(n) }

    override fun equals(other: Any?) = other is Vector<*, *, *> && components.equals(other.components)

    override fun toString() = components.joinToString(", ", "(", ")")

    override fun hashCode() = components.hashCode()
}

abstract class Vector2<T : Number, V : Vector2<T, V>>(numClass: KClass<T>, vecClass: KClass<V>, x: Number, y: Number) :
    Vector<Two, T, V>(numClass, vecClass, x, y) {
    var x by Component(0)
    var y by Component(1)

    @Suppress("UNCHECKED_CAST")
    fun perpendicular() = instanciate(arrayOf(0.to(numClass).numMinus(y), x))
}

class Vector2f(x: Number, y: Number) : Vector2<Float, Vector2f>(Float::class, Vector2f::class, x, y) {
    constructor(xy: Float = 0f) : this(xy, xy)
    constructor(v: Vector<*, *, *>, default: Float = 0f) : this(
        v.getOrElse(0, default),
        v.getOrElse(1, default)
    )

    companion object {
        fun random() = Vector2f(Random.nextFloat(), Random.nextFloat())
        fun randomSigned() = Vector2f(Random.nextFloatSigned(), Random.nextFloatSigned())
    }

    override fun instanciate(values: Array<Number>) = Vector2f(values[0], values[1])
}

class Vector2i(x: Number, y: Number) : Vector2<Int, Vector2i>(Int::class, Vector2i::class, x, y) {
    constructor(xy: Number = 0) : this(xy, xy)
    constructor(v: Vector<*, *, *>, default: Number = 0) : this(
        v.getOrElse(0, default),
        v.getOrElse(1, default)
    )

    override fun instanciate(values: Array<Number>) = Vector2i(values[0], values[1])
}

abstract class Vector3<T : Number, V : Vector3<T, V>>(numClass: KClass<T>, vecClass: KClass<V>, x: Number, y: Number, z: Number) :
    Vector<Three, T, V>(numClass, vecClass, x, y, z) {
    var x by Component(0)
    var y by Component(1)
    var z by Component(2)

    @Suppress("UNCHECKED_CAST")
    infix fun cross(other: V) = instanciate(
        arrayOf(
            y.numTimes(other.z).numMinus(z.numTimes(other.y)),
            z.numTimes(other.x).numMinus(x.numTimes(other.z)),
            x.numTimes(other.y).numMinus(y.numTimes(other.x))
        )
    )
}

class Vector3f(x: Number, y: Number, z: Number) : Vector3<Float, Vector3f>(Float::class, Vector3f::class, x, y, z) {
    constructor(xyz: Number = 0f) : this(xyz, xyz, xyz)
    constructor(v: Vector<*, *, *>, default: Number = 0f) : this(
        v.getOrElse(0, default),
        v.getOrElse(1, default),
        v.getOrElse(2, default)
    )

    companion object {
        fun random() = Vector3f(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
        fun randomSigned() = Vector3f(Random.nextFloatSigned(), Random.nextFloatSigned(), Random.nextFloatSigned())

        val right = Vector3f(1f, 0f, 0f)
        val left = -right
        val up = Vector3f(0f, 1f, 0f)
        val down = -up
        val back = Vector3f(0f, 0f, 1f)
        val front = -back
    }

    override fun instanciate(values: Array<Number>) = Vector3f(values[0], values[1], values[2])
}

abstract class Vector4<T : Number, V : Vector4<T, V>>(
    numClass: KClass<T>, vecClass: KClass<V>, x: Number, y: Number, z: Number, w: Number
) : Vector<Four, T, V>(numClass, vecClass, x, y, z, w) {
    var x by Component(0)
    var y by Component(1)
    var z by Component(2)
    var w by Component(3)
}

class Vector4f(x: Number, y: Number, z: Number, w: Number) :
    Vector4<Float, Vector4f>(Float::class, Vector4f::class, x, y, z, w) {
    constructor(xyzw: Number = 0f) : this(xyzw, xyzw, xyzw, xyzw)
    constructor(v: Vector<*, *, *>, default: Number = 0f) : this(
        v.getOrElse(0, default),
        v.getOrElse(1, default),
        v.getOrElse(2, default),
        v.getOrElse(3, default)
    )

    override fun instanciate(values: Array<Number>) = Vector4f(values[0], values[1], values[2], values[3])
}

class Color(r: Number, g: Number, b: Number, a: Number = 1f) :
    Vector<Four, Float, Color>(Float::class, Color::class, r, g, b, a) {
    constructor(gray: Number, a: Number = 1f) : this(gray, gray, gray, a)

    var r by Component(0)
    var g by Component(1)
    var b by Component(2)
    var a by Component(3)

    companion object {
        val black = Color(0f)
        val white = Color(1f)
    }

    override fun instanciate(values: Array<Number>) = Color(values[0], values[1], values[2], values[3])
}

class Rect(x1: Number, y1: Number, x2: Number, y2: Number) :
    Vector<Four, Float, Rect>(Float::class, Rect::class, x1, y1, x2, y2) {
    constructor(v1: Vector<Two, *, *>, v2: Vector<Two, *, *> = v1) :
            this(v1[0], v1[1], v2[0], v2[1])

    companion object {
        fun zero() = Rect(0f, 0f, 0f, 0f)
        fun one() = Rect(0f, 0f, 1f, 1f)
    }

    var x1 by Component(0)
    var y1 by Component(1)
    var x2 by Component(2)
    var y2 by Component(3)

    override fun instanciate(values: Array<Number>) = Rect(values[0], values[1], values[2], values[3])

    val start; get() = Vector2f(x1, y1)
    val end; get() = Vector2f(x2, y2)

    val size; get() = Vector2f(x2 - x1, y2 - y1)
    val center; get() = start + size / 2f

    operator fun contains(v: Vector2f) = v.x in x1..x2 && v.y in y1..y2

    fun randomPoint() = Vector2f(Random.nextFloat() * (x2 - x1) + x1, Random.nextFloat() * (y2 - y1) + y1)
}

class Quaternion(a: Number, b: Number, c: Number, d: Number) :
    Vector<Four, Float, Quaternion>(Float::class, Quaternion::class, a, b, c, d) {
    constructor() : this(1f, 0f, 0f, 0f)

    companion object {
        fun axisAngle(axis: Vector3f, angle: Float) = (axis * sin(angle / 2)).run {
            Quaternion(cos(angle / 2), x, y, z).normalize()
        }

        fun euler(x: Float = 0f, y: Float = 0f, z: Float = 0f) =
            (axisAngle(Vector3f.right, x) * axisAngle(Vector3f.up, y) * axisAngle(Vector3f.back, z)).normalize()
    }

    var a by Component(0)
    var b by Component(1)
    var c by Component(2)
    var d by Component(3)

    override fun instanciate(values: Array<Number>) = Quaternion(values[0], values[1], values[2], values[3])

    operator fun times(other: Quaternion) = Quaternion(
        a * other.a - b * other.b - c * other.c - d * other.d,
        a * other.b + b * other.a + c * other.d - d * other.c,
        a * other.c - b * other.d + c * other.a + d * other.b,
        a * other.d + b * other.c - c * other.b + d * other.a
    )

    fun matrix() = Matrix3(
        1 - 2 * (c * c + d * d), 2 * (b * c + d * a), 2 * (b * d - c * a),
        2 * (b * c - d * a), 1 - 2 * (b * b + d * d), 2 * (c * d + b * a),
        2 * (b * d + c * a), 2 * (c * d - b * a), 1 - 2 * (b * b + c * c),
    )

    fun conj() = Quaternion(a, -b, -c, -d)
}