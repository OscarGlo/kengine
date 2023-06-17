package kengine.util

import kengine.entity.components.render.gui.Text
import kengine.entity.components.render.r2d.Render2D
import kengine.math.Color
import kengine.math.Matrix4
import kengine.math.Vector2f
import kengine.math.Vector3f
import kengine.objects.Font
import kengine.objects.KERuntime
import kengine.objects.gl.*
import org.lwjgl.opengl.GL30.*

class DebugText(val text: String, time: Double) {
    val clearTime = KERuntime.doubleTime() + time
}

object Debug {
    private const val lineHeight = 1.2f
    private const val margin = 5
    private val font = Font(Resource.global("/fonts/JetBrainsMono.ttf"), 14)

    private val vertexArray = VertexArray()
    private val arrayBuffer = GLBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
    private val elementBuffer = GLBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)

    private val lines = mutableListOf<DebugText>()
    private var characterCount = 0

    fun init() {
        vertexArray.init().bind()
        arrayBuffer.run { init(); bind() }
        elementBuffer.run { init(); bind() }
        VertexAttributes(VertexAttribute(2), VertexAttribute(2)).use()

        font.texture.init()
    }

    private fun updateBuffers() {
        characterCount = lines.sumOf { it.text.length }

        vertexArray.bind()

        val textVertices = lines.mapIndexed { i, l ->
            Text.stringVertices(font, l.text, Vector2f(margin.toFloat(), -margin - i * (font.size * lineHeight + 2 * margin)))
        }
        val boxVertices = lines.indices.map { i ->
            val width = Text.calculateWidth(textVertices[i]) + margin * 2
            rectVertices(
                Vector2f(width, font.size * lineHeight + margin * 2),
                Vector2f(width / 2, margin - i * (font.size * lineHeight + 2 * margin))
            )
        }.fold(floatArrayOf(), FloatArray::plus)
        arrayBuffer.store(boxVertices + textVertices.fold(floatArrayOf(), FloatArray::plus))

        elementBuffer.store(rectIndicesN(lines.size + characterCount))
    }

    fun print(text: String, time: Double) {
        lines.add(0, DebugText(text, time))
        updateBuffers()
    }

    fun update() {
        var i = 0
        var removed = false
        while (i < lines.size) {
            val line = lines[i]
            if (line.clearTime < KERuntime.doubleTime()) {
                lines.removeAt(i)
                removed = true
            } else i++
        }
        if (removed) updateBuffers()
    }

    fun render() {
        if (characterCount == 0) return

        val size = KERuntime.window.size

        vertexArray.bind()
        arrayBuffer.bind()
        elementBuffer.bind()

        fun Shader.bindParams(color: Color) {
            this.use()
            this["model"] = Matrix4(position = Vector3f(-size.x / 2f, size.y / 2f - font.size * lineHeight, 0f))
            this["view"] = KERuntime.scene.view(fixed = true)
            this["projection"] = Matrix4()
            this["color"] = color
        }

        // Boxes
        Render2D.colorShader.bindParams(Color(0f, 0f, 0f, 0.5f))
        glDrawElements(GL_TRIANGLES, 6 * lines.size, GL_UNSIGNED_INT, 0)

        // Text
        Render2D.imageShader.bindParams(Color.white)
        font.texture.bind()
        glDrawElements(GL_TRIANGLES, 6 * characterCount, GL_UNSIGNED_INT, 6L * lines.size * sizeof(GL_INT).toLong())
    }
}