package kengine.math

import kengine.util.terminateError
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
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

    operator fun <V : Vector<S, Float, V>> times(v: V) = v::class.primaryConstructor!!.call(
        *transpose().values.mapIndexed { i, c -> c.map { f -> f * v[i] }.sum() }.toTypedArray()
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

    companion object {
        fun rotateZ(angle: Float) = Matrix3(
            mutableListOf(
                mutableListOf(cos(angle), -sin(angle), 0f),
                mutableListOf(sin(angle), cos(angle), 0f),
                mutableListOf(0f, 0f, 1f),
            )
        )
    }
}

class Matrix4(values: MatrixValues) : Matrix<Four, Matrix4>(Four::class, Matrix4::class, values) {
    constructor() : this(identity(Four::class))
    constructor(m: Matrix4) : this(m.values)
    constructor(v: Vector4f) : this() {
        setDiagonal(v)
    }

    var position: Vector3f
        get() = Vector3f(values[0][3], values[1][3], values[2][3])
        set(v) {
            values[0][3] = v.x
            values[1][3] = v.y
            values[2][3] = v.z
        }

    var rotationScale: Matrix3
        get() = Matrix3(
            values.slice(0..2).map {
                it.slice(0..2).toMutableList()
            }.toMutableList()
        )
        set(m) = m.values.forEachIndexed { x, l -> l.forEachIndexed { y, v -> values[x][y] = v } }

    var scaling: Vector3f
        get() = Vector3f(
            Vector3f(values[0][0], values[1][0], values[2][0]).length(),
            Vector3f(values[0][1], values[1][1], values[2][1]).length(),
            Vector3f(values[0][2], values[1][2], values[2][2]).length(),
        )
        set(v) {
            rotationScale = rotation * Matrix3(v)
        }

    var rotation: Matrix3
        get() = rotationScale * Matrix3(Vector3f(1f) / scaling)
        set(m) {
            rotationScale = m * Matrix3(scaling)
        }

    fun translate(v: Vector3f) = apply { position += v }
    fun scale(v: Vector3f) = apply { scaling *= v }
    fun rotate(m: Matrix3) = apply { rotationScale *= m }
}