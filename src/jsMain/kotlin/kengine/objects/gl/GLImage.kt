package kengine.objects.gl

import kengine.math.Vector2i
import kengine.objects.Image
import kengine.util.Resource
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.WebGLTexture
import org.w3c.files.FileReader
import org.khronos.webgl.WebGLRenderingContext as GL

actual class GLImage actual constructor(resource: Resource, bpp: Int, val filter: Boolean) : Image(resource, bpp) {
    lateinit var texture: WebGLTexture

    actual val size = Vector2i()

    actual suspend fun init() {
        if (this::texture.isInitialized) return

        texture = gl.createTexture()!!

        gl.bindTexture(GL.TEXTURE_2D, texture)

        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_S, GL.CLAMP_TO_EDGE)
        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_WRAP_T, GL.CLAMP_TO_EDGE)
        val filterType = if (filter) GL.LINEAR else GL.NEAREST
        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, filterType)
        gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, filterType)

        val blob = resource.getBlob()
        window.createImageBitmap(blob).then {
            size.x = it.width
            size.y = it.height
        }.await()

        FileReader().apply {
            onload = {
                bind()
                gl.texImage2D(
                    GL.TEXTURE_2D, 0,
                    GL.RGBA, size.x, size.y, 0,
                    GL.RGBA,
                    GL.UNSIGNED_BYTE, result.unsafeCast<ArrayBufferView>()
                )
            }
            readAsArrayBuffer(blob)
        }
    }

    actual fun bind() = gl.bindTexture(GL.TEXTURE_2D, texture)
}