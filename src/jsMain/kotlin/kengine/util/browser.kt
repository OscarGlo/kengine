package kengine.util

import kotlinx.browser.document
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement
import org.w3c.files.Blob
import org.w3c.files.FileReader
import kotlin.js.Promise

val canvas = document.querySelector("canvas") as HTMLCanvasElement
val gl = canvas.getContext("webgl2") as WebGLRenderingContext

fun Blob.arrayBuffer(): Promise<ArrayBuffer> = Promise { res, _ ->
    FileReader().apply {
        onload = { res(result as ArrayBuffer) }
        readAsArrayBuffer(this@arrayBuffer)
    }
}