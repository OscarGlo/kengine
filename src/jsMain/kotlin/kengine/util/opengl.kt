package kengine.util

import org.khronos.webgl.WebGLRenderingContext as GL

actual fun terminateError(message: String): Nothing = error(message)

actual val GL_BYTE: Int = GL.BYTE
actual val GL_UNSIGNED_BYTE: Int = GL.UNSIGNED_BYTE
actual val GL_SHORT: Int = GL.SHORT
actual val GL_UNSIGNED_SHORT: Int = GL.UNSIGNED_SHORT
actual val GL_INT = GL.INT
actual val GL_UNSIGNED_INT = GL.UNSIGNED_INT
actual val GL_FLOAT = GL.FLOAT