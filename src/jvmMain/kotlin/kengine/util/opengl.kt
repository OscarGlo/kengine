package kengine.util

import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.opengl.GL30

actual fun terminateError(message: String): Nothing {
    glfwTerminate()
    error(message)
}

actual val GL_BYTE = GL30.GL_BYTE
actual val GL_UNSIGNED_BYTE = GL30.GL_UNSIGNED_BYTE
actual val GL_SHORT =  GL30.GL_SHORT
actual val GL_UNSIGNED_SHORT = GL30.GL_UNSIGNED_SHORT
actual val GL_INT = GL30.GL_INT
actual val GL_UNSIGNED_INT = GL30.GL_UNSIGNED_INT
actual val GL_FLOAT = GL30.GL_FLOAT

actual val GL_VERTEX_SHADER = GL30.GL_VERTEX_SHADER
actual val GL_FRAGMENT_SHADER = GL30.GL_FRAGMENT_SHADER