package kengine.tools

import kengine.entity.components.render.gui.Text
import kengine.entity.components.render.r2d.Render2D
import kengine.math.Color
import kengine.math.Matrix4
import kengine.math.Vector2f
import kengine.math.Vector3f
import kengine.objects.Font
import kengine.objects.KERuntime
import kengine.objects.gl.*
import kengine.util.rectIndicesN
import kengine.util.rectVertices
import kengine.util.sizeof
import org.lwjgl.opengl.GL30.*

object Debug {
    private const val lineHeight = 1.2f
    private const val margin = 5
    private val font = Font(Resource.global("/fonts/JetBrainsMono.ttf"), 14)

    private val vertexArray = VertexArray()
    private val arrayBuffer = GLBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
    private val elementBuffer = GLBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)

    class Line(val text: String, time: Double, val color: Color) {
        val clearTime = KERuntime.doubleTime() + time
    }

    private val lines = mutableListOf<Line>()

    fun init() {
        vertexArray.init().bind()
        arrayBuffer.run { init(); bind() }
        elementBuffer.run { init(); bind() }
        VertexAttributes(VertexAttribute(2), VertexAttribute(2)).use()

        font.texture.init()
    }

    private fun updateBuffers() {
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

        elementBuffer.store(rectIndicesN(lines.size + lines.sumOf { it.text.length }))
    }

    fun print(text: String, time: Double = 3.0, color: Color = Color.white) {
        lines.add(0, Line(text, time, color))
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
        if (lines.isEmpty()) return

        val size = KERuntime.window.size

        vertexArray.bind()
        arrayBuffer.bind()
        elementBuffer.bind()

        fun Shader.bindParams() {
            this.use()
            this["model"] = Matrix4(position = Vector3f(-size.x / 2f, size.y / 2f - font.size * lineHeight, 0f))
            this["view"] = KERuntime.scene.view(fixed = true)
            this["projection"] = Matrix4()
        }

        // Boxes
        Render2D.colorShader.bindParams()
        Render2D.colorShader["color"] = Color(0f, 0f, 0f, 0.5f)
        glDrawElements(GL_TRIANGLES, 6 * lines.size, GL_UNSIGNED_INT, 0)

        // Text
        Render2D.imageShader.bindParams()
        var chars = 0
        lines.forEach {
            Render2D.imageShader["color"] = it.color
            font.texture.bind()
            glDrawElements(GL_TRIANGLES, 6 * it.text.length, GL_UNSIGNED_INT, 6L * (lines.size + chars) * sizeof(GL_INT).toLong())
            chars += it.text.length
        }
    }
}