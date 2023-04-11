package kengine.entity.components.render

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.math.Matrix4
import kengine.objects.gl.*
import kengine.util.sizeof
import org.lwjgl.opengl.GL30.*
import kotlin.reflect.KClass

abstract class Render(
    protected val vertices: FloatArray,
    private val indices: IntArray,
    protected vararg val images: GLImage
) : Entity.Component() {
    override val required: List<KClass<out Entity.Component>> = listOf(Transform::class)

    abstract val defaultAttributes: VertexAttributes

    protected val vertexArray = VertexArray()
    protected val arrayBuffer = GLBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW)
    protected val elementBuffer = GLBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW)
    protected var depthTest = true

    var visible = true

    override fun initialize() {
        vertexArray.init().bind()

        arrayBuffer.init().store(vertices)
        elementBuffer.init().store(indices)

        images.forEach { it.init() }

        defaultAttributes.use()
    }

    open fun model() = entity.get<Transform>().global()

    open fun projection() = root.currentCamera?.projection() ?: Matrix4()

    open fun view() = entity.get<Transform>().let {
        root.view(it.is3D, it.fixed)
    }

    fun bindShader(shader: Shader, vararg params: Pair<String, Any>) {
        shader.use()
        shader["model"] = model()
        shader["view"] = view()
        shader["projection"] = projection()
        params.forEach { (name, value) -> shader[name] = value }
    }

    private var vertexOffset = 0

    protected fun triangles(i: Int) {
        glDrawElements(
            GL_TRIANGLES,
            3 * i,
            GL_UNSIGNED_INT,
            3 * vertexOffset * sizeof(GL_INT).toLong()
        )
        vertexOffset += i
    }

    open fun render() {
        if (!visible) return

        vertexArray.bind()
        elementBuffer.bind()

        vertexOffset = 0
        if (depthTest) glEnable(GL_DEPTH_TEST)
        else glDisable(GL_DEPTH_TEST)
        renderSteps()
    }

    abstract fun renderSteps()
}