package kengine.math

import kengine.util.terminateError
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
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

    operator fun times(v: Vector<S, Float>) = kClass.primaryConstructor!!.call(
        values.mapIndexed { i, l ->
            l.map { f -> f * v[i] }.toMutableList()
        }.toMutableList()
    )

    operator fun times(m: M): M {
        val values = Array(size) { Array(size) { 0f }.toMutableList() }.toMutableList()
        for (x in 0 until size) {
            for (y in 0 until size) {
                values[x][y] = (0 until size).fold(0f) { acc, i -> acc + this[i, y] * m[x, i] }
            }
        }
        return kClass.primaryConstructor!!.call(values)
    }

    fun toBuffer(): FloatBuffer = BufferUtils.createFloatBuffer(16).put(values.flatten().toFloatArray())

    override fun toString() = values.joinToString("\n")
}

class Matrix3(values: MatrixValues) : Matrix<Three, Matrix3>(Three::class, Matrix3::class, values) {
    constructor() : this(identity(Three::class))
}

class Matrix4(values: MatrixValues) : Matrix<Four, Matrix4>(Four::class, Matrix4::class, values) {
    constructor() : this(identity(Four::class))

    var translation: Vector3<Float>
        get() = Vector3.new(values[0][3], values[1][3], values[2][3])
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

    var scale: Vector3<Float>
        get() = Vector3.new(
            Vector3.new(values[0][0], values[1][0], values[2][0]).length(),
            Vector3.new(values[0][1], values[1][1], values[2][1]).length(),
            Vector3.new(values[0][2], values[1][2], values[2][2]).length(),
        )
        set(v) {
            rotationScale = rotation * v
        }

    var rotation: Matrix3
        get() = rotationScale * (Vector3.new(1f) / scale)
        set(m) {
            rotationScale = m * scale
        }
}