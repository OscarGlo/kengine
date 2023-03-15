package kengine.entity.components.render

import kengine.entity.Entity
import kengine.entity.components.Transform2D
import kengine.math.Color
import kengine.math.Matrix4
import kengine.objects.gl.*
import kengine.util.sizeof
import org.lwjgl.opengl.GL30.*

abstract class Render(
    protected val vertices: FloatArray,
    private val indices: IntArray,
    protected vararg val images: Image
) : Entity.Component() {
    companion object {
        val defaultAttributes = VertexAttributes(VertexAttribute(2), VertexAttribute(2))

        val colorShader = Shader(
            GL_VERTEX_SHADER to "/shaders/base.vert",
            GL_FRAGMENT_SHADER to "/shaders/solidColor.frag"
        )

        val imageShader = Shader(
            GL_VERTEX_SHADER to "/shaders/base.vert",
            GL_FRAGMENT_SHADER to "/shaders/modulateTexture.frag"
        )

        fun init() {
            colorShader.init()
            imageShader.init()
        }
    }

    protected val vertexArray = VertexArray()
    protected val arrayBuffer = GLBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
    protected val elementBuffer = GLBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)

    var visible = true

    override fun initialize() {
        vertexArray.init().bind()

        arrayBuffer.init().store(vertices)
        elementBuffer.init().store(indices)

        images.forEach { it.init() }

        defaultAttributes.use()
    }

    private var vertexOffset = 0

    open fun transform() = entity.get<Transform2D>().viewport()
    private lateinit var currentTransform: Matrix4

    fun bindShader(shader: Shader, color: Color) {
        shader.use()
        shader["transform"] = currentTransform
        shader["color"] = color
    }

    protected fun triangles(i: Int) {
        glDrawElements(GL_TRIANGLES, 3 * i, GL_UNSIGNED_INT, 3 * vertexOffset * sizeof(GL_INT).toLong())
        vertexOffset += i
    }

    protected fun colored(i: Int, color: Color) {
        bindShader(colorShader, color)
        triangles(i)
    }

    protected fun textured(i: Int, image: Image, color: Color = Color.white) {
        bindShader(imageShader, color)
        image.bind()
        triangles(i)
    }

    fun render() {
        if (!visible) return

        vertexArray.bind()
        elementBuffer.bind()

        vertexOffset = 0
        currentTransform = transform()
        renderSteps()
    }

    abstract fun renderSteps()
}