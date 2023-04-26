package kengine.entity.components.render

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.math.Matrix4
import kengine.objects.KERuntime
import kengine.objects.gl.*
import kengine.util.DEPTH_TEST
import kengine.util.glDisable
import kengine.util.glDrawTriangles
import kengine.util.glEnable
import kotlin.reflect.KClass

// TODO: Global opengl wrappers
abstract class Render(
    protected val vertices: FloatArray,
    private val indices: IntArray,
    protected vararg val images: GLImage
) : Entity.Component() {
    override val required: List<KClass<out Entity.Component>> = listOf(Transform::class)

    abstract val defaultAttributes: VertexAttributes

    protected val vertexArray = VertexArray()
    protected val arrayBuffer = GLBuffer(GLBuffer.ARRAY, GLBuffer.STATIC_DRAW)
    protected val elementBuffer = GLBuffer(GLBuffer.ELEMENT_ARRAY, GLBuffer.STATIC_DRAW)
    protected var depthTest = true

    var visible = true

    override suspend fun init() {
        vertexArray.init().bind()

        arrayBuffer.init().store(vertices)
        elementBuffer.init().store(indices)

        images.forEach { it.init() }

        defaultAttributes.use()
    }

    open fun model() = entity.get<Transform>().global()

    open fun projection() = KERuntime.scene.currentCamera?.projection() ?: Matrix4()

    open fun view() = entity.get<Transform>().let {
        KERuntime.scene.view(it.is3D, it.fixed)
    }

    fun bindShader(shader: Shader, vararg params: Pair<String, Any>) {
        shader.use()
        shader["model"] = model()
        shader["view"] = view()
        shader["projection"] = projection()
        params.forEach { (name, value) -> shader[name] = value }
    }

    private var offset = 0

    protected fun triangles(i: Int) {
        glDrawTriangles(i, offset)
        offset += i
    }

    open fun render() {
        if (!visible) return

        vertexArray.bind()
        elementBuffer.bind()

        offset = 0
        if (depthTest) glEnable(DEPTH_TEST)
        else glDisable(DEPTH_TEST)
        renderSteps()
    }

    abstract fun renderSteps()
}