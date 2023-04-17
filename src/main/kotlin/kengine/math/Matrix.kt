package kengine.math

import kengine.util.terminateError
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import kotlin.math.max
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

typealias MatrixValues = MutableList<MutableList<Float>>

open class Matrix<S : Size, M : Matrix<S, M>>(
    sizeClass: KClass<S>, private val kClass: KClass<M>, val values: MatrixValues
) {
    companion object {
        fun <S : Size> identity(sizeClass: KClass<S>) = (0 until Size.value(sizeClass)).map { i ->
            (0 until Size.value(sizeClass)).map { j ->
                if (i == j) 1f else 0f
            }.toMutableList()
        }.toMutableList()
    }

    val size = Size.value(sizeClass)

    operator fun get(x: Int, y: Int) =
        values.getOrNull(x)?.getOrNull(y) ?: terminateError("Invalid matrix position $x, $y")

    operator fun set(x: Int, y: Int, f: Float) {
        if (x !in 0 until values.size || y !in 0 until values.size) terminateError("Invalid matrix position $x, $y")
        values[x][y] = f
    }

    @Suppress("UNCHECKED_CAST")
    fun <V : Vector<S, Float, V>> setDiagonal(v: V) = apply {
        values.mapIndexed { i, l -> l[i] = v[i] }
    } as M

    private fun componentwise(m: M, f: (Float, Float) -> Float) =
        kClass.primaryConstructor!!.call(values.mapIndexed { x, c ->
            c.mapIndexed { y, v -> f(v, m[x, y]) }.toMutableList()
        }.toMutableList())

    operator fun plus(m: M) = componentwise(m, Float::plus)
    operator fun minus(m: M) = componentwise(m, Float::minus)

    operator fun times(f: Float) =
        kClass.primaryConstructor!!.call(values.map { c -> c.map { it * f }.toMutableList() }.toMutableList())

    operator fun <V : Vector<S, Float, V>> times(v: V) = v::class.primaryConstructor!!.call(
        *values.map { c -> c.mapIndexed { i, f -> f * v[i] }.sum() }.toTypedArray()
    )

    operator fun times(m: M): M {
        val values = Array(size) { Array(size) { 0f }.toMutableList() }.toMutableList()
        for (x in 0 until size)
            for (y in 0 until size)
                values[x][y] = (0 until size).fold(0f) { acc, i -> acc + this[x, i] * m[i, y] }
        return kClass.primaryConstructor!!.call(values)
    }

    fun transpose() = kClass.primaryConstructor!!.call(MutableList(size) { i -> values.map { row -> row[i] } })

    fun toBuffer(): FloatBuffer =
        BufferUtils.createFloatBuffer(size * size).put(transpose().values.flatten().toFloatArray()).flip()

    override fun toString(): String {
        val maxLen = values.flatten().fold(0) { a, b -> max(a, "%.3f".format(b).length) }
        return values.joinToString("\n") {
            it.joinToString(" ", "[", "]") { "%${maxLen}.3f".format(it) }
        }
    }
}

class Matrix3(values: MatrixValues) : Matrix<Three, Matrix3>(Three::class, Matrix3::class, values) {
    constructor() : this(identity(Three::class))
    constructor(m: Matrix3) : this(m.values)
    constructor(v: Vector3f) : this() {
        setDiagonal(v)
    }
}

class Matrix4(values: MatrixValues) : Matrix<Four, Matrix4>(Four::class, Matrix4::class, values) {
    constructor() : this(identity(Four::class))
    constructor(m: Matrix4) : this(m.values)
    constructor(v: Vector4f) : this() {
        setDiagonal(v)
    }
    constructor(position: Vector3f = Vector3f(), scaling: Vector3f = Vector3f(), rotation: Quaternion = Quaternion()) : this(
        rotation.matrix().let {
            mutableListOf(
                mutableListOf(scaling.x * it[0, 0], scaling.y * it[1, 0], scaling.z * it[2, 0], position.x),
                mutableListOf(scaling.x * it[0, 1], scaling.y * it[1, 1], scaling.z * it[2, 1], position.y),
                mutableListOf(scaling.x * it[0, 2], scaling.y * it[1, 2], scaling.z * it[2, 2], position.z),
                mutableListOf(0f, 0f, 0f, 1f)
            )
        }
    )
}