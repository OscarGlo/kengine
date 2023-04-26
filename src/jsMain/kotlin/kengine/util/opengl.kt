package kengine.util

import org.khronos.webgl.WebGLRenderingContext as GL

actual fun terminateError(message: String): Nothing = error(message)

actual fun terminate() {}

actual fun glEnable(cap: Int) = gl.enable(cap)
actual fun glDisable(cap: Int) = gl.disable(cap)

actual fun glDrawTriangles(count: Int, offset: Int) = gl.drawElements(
    GL.TRIANGLES,
    3 * count,
    GL.UNSIGNED_INT,
    3 * offset * sizeof(GL.UNSIGNED_INT)
)

actual val BYTE: Int = GL.BYTE
actual val UNSIGNED_BYTE: Int = GL.UNSIGNED_BYTE
actual val SHORT: Int = GL.SHORT
actual val UNSIGNED_SHORT: Int = GL.UNSIGNED_SHORT
actual val INT = GL.INT
actual val UNSIGNED_INT = GL.UNSIGNED_INT
actual val FLOAT = GL.FLOAT

actual val DEPTH_TEST = GL.DEPTH_TEST