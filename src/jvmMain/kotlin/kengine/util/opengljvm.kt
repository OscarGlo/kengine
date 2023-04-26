package kengine.util

import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.opengl.GL30

actual fun terminateError(message: String): Nothing {
    glfwTerminate()
    error(message)
}

actual fun terminate() = glfwTerminate()

actual fun glEnable(cap: Int) = GL30.glEnable(cap)
actual fun glDisable(cap: Int) = GL30.glDisable(cap)

actual fun glDrawTriangles(count: Int, offset: Int) = GL30.glDrawElements(
    GL30.GL_TRIANGLES,
    3 * count,
    GL30.GL_UNSIGNED_INT,
    3 * offset * sizeof(GL30.GL_UNSIGNED_INT).toLong()
)

actual val BYTE = GL30.GL_BYTE
actual val UNSIGNED_BYTE = GL30.GL_UNSIGNED_BYTE
actual val SHORT =  GL30.GL_SHORT
actual val UNSIGNED_SHORT = GL30.GL_UNSIGNED_SHORT
actual val INT = GL30.GL_INT
actual val UNSIGNED_INT = GL30.GL_UNSIGNED_INT
actual val FLOAT = GL30.GL_FLOAT

actual val DEPTH_TEST = GL30.GL_DEPTH_TEST