package kengine.objects.gl

import kotlinx.browser.document
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement

val canvas = document.querySelector("canvas") as HTMLCanvasElement
val gl = canvas.getContext("webgl2") as WebGLRenderingContext