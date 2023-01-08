package util

import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.opengl.GL41.*
import kotlin.system.exitProcess

fun sizeof(type: Int) = when (type) {
    GL_BYTE -> Byte.SIZE_BYTES
    GL_UNSIGNED_BYTE -> Byte.SIZE_BYTES
    GL_SHORT -> Short.SIZE_BYTES
    GL_UNSIGNED_SHORT -> Short.SIZE_BYTES
    GL_INT -> Int.SIZE_BYTES
    GL_UNSIGNED_INT -> Int.SIZE_BYTES
    GL_FIXED -> Int.SIZE_BYTES
    GL_HALF_FLOAT -> Float.SIZE_BYTES / 2
    GL_FLOAT -> Float.SIZE_BYTES
    GL_DOUBLE -> Double.SIZE_BYTES
    else -> terminateError("Size of type $type is not defined")
}

fun bool(b: Boolean) = when (b) {
    true -> GL_TRUE
    false -> GL_FALSE
}

fun terminateError(message: String): Nothing {
    System.err.println(message)
    Thread.dumpStack()
    glfwTerminate()
    exitProcess(1)
}

fun rectVertices(
    width: Float,
    height: Float,
    offset: Vector2f = Vector2f(0f, 0f),
    uv: Vector4f = Vector4f(0f, 0f, 1f, 1f)
): FloatArray {
    val w = width / 2
    val h = height / 2
    return floatArrayOf(
        offset.x + w, offset.y + h, uv.z, uv.y,
        offset.x - w, offset.y + h, uv.x, uv.y,
        offset.x - w, offset.y - h, uv.x, uv.w,
        offset.x + w, offset.y - h, uv.z, uv.w
    )
}

val rectIndices = intArrayOf(0, 1, 3, 1, 2, 3)

fun rectIndicesN(count: Int = 1) =
    List(count) { rectIndices }
        .mapIndexed { i, a -> a.map { it + i * 4 } }
        .reduce { a, b -> a + b }
        .toIntArray()

