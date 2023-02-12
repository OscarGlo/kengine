package util

import org.joml.*

val black = Vector4f(0f, 0f, 0f, 1f)
val white = Vector4f(1f, 1f, 1f, 1f)

operator fun Matrix4f.times(other: Matrix4f): Matrix4f = Matrix4f(this).mul(other)
fun Matrix4f.roundTransform(): Matrix4f =
    Matrix4f(this).setTranslation(transformPosition(Vector3f()).round())

operator fun Vector2f.times(other: Vector2f): Vector2f = mul(other, Vector2f())

fun Vector2i.f() = Vector2f(x.toFloat(), y.toFloat())