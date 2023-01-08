package util

import org.joml.Matrix4f
import org.joml.Vector4f

val black = Vector4f(0f, 0f, 0f, 1f)
val white = Vector4f(1f, 1f, 1f, 1f)

operator fun Matrix4f.times(other: Matrix4f): Matrix4f = mulLocal(other, Matrix4f())