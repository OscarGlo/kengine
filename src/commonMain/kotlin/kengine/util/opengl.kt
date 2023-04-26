package kengine.util

import kengine.math.Rect
import kengine.math.Vector2f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

expect fun terminateError(message: String): Nothing
expect fun terminate()

expect fun glEnable(cap: Int)
expect fun glDisable(cap: Int)

expect fun glDrawTriangles(count: Int, offset: Int)

expect val BYTE: Int
expect val UNSIGNED_BYTE: Int
expect val SHORT: Int
expect val UNSIGNED_SHORT: Int
expect val INT: Int
expect val UNSIGNED_INT: Int
expect val FLOAT: Int

expect val DEPTH_TEST: Int

fun sizeof(type: Int) = when (type) {
    BYTE -> Byte.SIZE_BYTES
    UNSIGNED_BYTE -> Byte.SIZE_BYTES
    SHORT -> Short.SIZE_BYTES
    UNSIGNED_SHORT -> Short.SIZE_BYTES
    INT -> Int.SIZE_BYTES
    UNSIGNED_INT -> Int.SIZE_BYTES
    FLOAT -> Float.SIZE_BYTES
    else -> terminateError("Size of type $type is not defined")
}

fun boolInt(b: Boolean) = if (b) 1 else 0

fun rectVertices(
    size: Vector2f,
    offset: Vector2f = Vector2f(0f, 0f),
    uv: Rect = Rect.one()
): FloatArray {
    val w = size.x / 2
    val h = size.y / 2
    return floatArrayOf(
        offset.x + w, offset.y + h, uv.x2, uv.y1,
        offset.x - w, offset.y + h, uv.x1, uv.y1,
        offset.x - w, offset.y - h, uv.x1, uv.y2,
        offset.x + w, offset.y - h, uv.x2, uv.y2
    )
}

val rectIndices = intArrayOf(0, 1, 3, 1, 2, 3)

fun rectIndicesN(count: Int) = (0 until count)
    .flatMap { i -> rectIndices.map { it + i * 4 } }
    .toIntArray()

fun ellipseVertices(
    size: Vector2f,
    count: Int,
    offset: Vector2f = Vector2f(0f, 0f),
    uv: Rect = Rect.one()
): FloatArray {
    val uvCenter = Vector2f(uv.x1 + uv.x2 / 2f, uv.y1 + uv.y2 / 2f)
    val uvSize = uv.size / 2f
    return floatArrayOf(
        offset.x, offset.y, uvCenter.x, uvCenter.y,
        *(0 until count)
            .flatMap { i ->
                val a = 2 * PI.toFloat() * i / count
                listOf(
                    sin(a) * size.x / 2,
                    cos(a) * size.y / 2,
                    uvCenter.x + sin(a) * uvSize.x,
                    uvCenter.y + cos(a) * uvSize.y
                )
            }
            .toFloatArray()
    )
}

fun ellipseIndices(count: Int) = (0..count)
    .flatMap { i -> listOf(0, i, i % count + 1) }
    .toIntArray()

fun gridUvs(rows: Int, cols: Int, x: Int, y: Int) =
    Rect(x.toFloat() / cols, y.toFloat() / rows, (x.toFloat() + 1) / cols, (y.toFloat() + 1) / rows)