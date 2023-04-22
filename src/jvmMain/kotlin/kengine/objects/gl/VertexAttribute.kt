@file:JvmName("VertexAttributeKt")

package kengine.objects.gl

import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer

actual fun VertexAttribute.bind(index: Int, offset: Int, total: Int) {
    glVertexAttribPointer(index, size, type, normalized, total, offset.toLong())
    glEnableVertexAttribArray(index)
}