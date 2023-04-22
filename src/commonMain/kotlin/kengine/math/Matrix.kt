package kengine.math

import kengine.util.format
import kengine.util.terminateError
import kotlin.math.max
import kotlin.reflect.KClass

abstract class Matrix<S : Size, M : Matrix<S, M>>(
    sizeClass: KClass<S>, private val kClass: KClass<M>, val values: FloatArray
) {
    companion object {
        fun <S : Size> identity(sizeClass: KClass<S>) = Size.value(sizeClass).let { size ->
            (0 until size * size).map { i ->
                if (i % size == i / size) 1f else 0f
            }.toFloatArray()
        }
    }

    val size = Size.value(sizeClass)

    abstract fun instanciate(values: FloatArray): M

    operator fun get(x: Int, y: Int) =
        values.getOrNull(x + size * y) ?: terminateError("Invalid matrix position $x, $y")

    operator fun set(x: Int, y: Int, f: Float) {
        if (x + size * y !in values.indices) terminateError("Invalid matrix position $x, $y")
        values[x + size * y] = f
    }

    @Suppress("UNCHECKED_CAST")
    fun <V : Vector<S, Float, V>> setDiagonal(v: V) = apply {
        v.components.forEachIndexed { i, f -> this[i, size * i] = f }
    } as M

    private fun componentwise(m: M, f: (Float, Float) -> Float) =
        instanciate(values.mapIndexed { i, v -> f(v, m.values[i]) }.toFloatArray())

    operator fun plus(m: M) = componentwise(m, Float::plus)
    operator fun minus(m: M) = componentwise(m, Float::minus)

    operator fun times(f: Float) =
        instanciate(values.map { it * f }.toFloatArray())

    operator fun <V : Vector<S, Float, V>> times(v: V) = v.instanciate(
        values.asList().chunked(size).map { c -> c.mapIndexed { i, f -> f * v[i] }.sum() }.toTypedArray()
    )

    operator fun times(m: M): M {
        val values = FloatArray(size * size) { 0f }
        for (x in 0 until size)
            for (y in 0 until size)
                values[x + size * y] = (0 until size).fold(0f) { acc, i -> acc + this[x, i] * m[i, y] }
        return instanciate(values)
    }

    override fun toString(): String {
        val maxLen = values.fold(0) { a, b -> max(a, "%.3f".format(b).length) }
        return values.asList().chunked(size).joinToString("\n") {
            it.joinToString(" ", "[", "]") { "%${maxLen}.3f".format(it) }
        }
    }
}

class Matrix3(vararg values: Float) : Matrix<Three, Matrix3>(Three::class, Matrix3::class, values) {
    constructor() : this(*identity(Three::class))
    constructor(m: Matrix3) : this(*m.values)
    constructor(v: Vector3f) : this() {
        setDiagonal(v)
    }

    override fun instanciate(values: FloatArray) = Matrix3(*values)
}

class Matrix4(vararg values: Float) : Matrix<Four, Matrix4>(Four::class, Matrix4::class, values) {
    constructor() : this(*identity(Four::class))
    constructor(m: Matrix4) : this(*m.values)
    constructor(v: Vector4f) : this() {
        setDiagonal(v)
    }
    constructor(position: Vector3f = Vector3f(), scaling: Vector3f = Vector3f(1f), rotation: Quaternion = Quaternion()) : this(
        *rotation.matrix().let {
            floatArrayOf(
                scaling.x * it[0, 0], scaling.x * it[1, 0], scaling.x * it[2, 0], 0f,
                scaling.y * it[0, 1], scaling.y * it[1, 1], scaling.y * it[2, 1], 0f,
                scaling.z * it[0, 2], scaling.z * it[1, 2], scaling.z * it[2, 2], 0f,
                position.x,           position.y,           position.z,           1f
            )
        }
    )

    override fun instanciate(values: FloatArray) = Matrix4(*values)

    var position
        get() = Vector3f(this[0, 3], this[1, 3], this[2, 3])
        set(p) {
            this[0, 3] = p[0]
            this[1, 3] = p[1]
            this[2, 3] = p[2]
        }
}