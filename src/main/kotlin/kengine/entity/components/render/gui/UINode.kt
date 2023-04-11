package kengine.entity.components.render.gui

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.entity.components.render.r2d.Render2D
import kengine.math.*
import kengine.objects.gl.GLImage
import kotlin.reflect.KClass

abstract class UINode(
    vertices: FloatArray,
    indices: IntArray,
    vararg images: GLImage
) :
    Render2D(vertices, indices, *images) {
    override val required: List<KClass<out Entity.Component>> = emptyList()

    class Position(
        var top: Float? = null,
        var bottom: Float? = null,
        var left: Float? = null,
        var right: Float? = null
    )

    var position: Position = Position()
    lateinit var theme: Theme

    fun with(position: Position) = this.apply { this.position = position }
    fun with(theme: Theme) = this.apply { this.theme = theme }

    override fun initialize() {
        if (!::theme.isInitialized)
            theme =
                if (entity.parent != null && entity.parent!!.has<UINode>()) entity.parent!!.get<UINode>().theme
                else Theme.default

        super.initialize()
        theme.font.texture.init()
    }

    abstract fun size(): Vector2f

    protected fun parentBounds(): Rect =
        if (entity.parent == null || !entity.parent!!.has<UINode>()) {
            val winSize = root.window.size
            Rect(-winSize.x / 2f, -winSize.y / 2f, winSize.x / 2f, winSize.y / 2f)
        } else {
            entity.parent!!.get<UINode>().bounds()
        }

    @Suppress("DuplicatedCode")
    open fun bounds() = Rect.zero().apply {
        val size = size()
        val parentBounds = parentBounds()

        if (position.left != null && position.right != null) {
            x1 = parentBounds.x1 + position.left!!
            x2 = parentBounds.x2 - position.right!!
        } else if (position.left != null) {
            x1 = parentBounds.x1 + position.left!!
            x2 = parentBounds.x1 + position.left!! + size.x
        } else if (position.right != null) {
            x1 = parentBounds.x2 - position.right!! - size.x
            x2 = parentBounds.x2 - position.right!!
        } else {
            x1 = parentBounds.center.x - size.x / 2f
            x2 = parentBounds.center.x + size.x / 2f
        }

        if (position.bottom != null && position.top != null) {
            y1 = parentBounds.y1 + position.bottom!!
            y2 = parentBounds.y2 - position.top!!
        } else if (position.bottom != null) {
            y1 = parentBounds.y1 + position.bottom!!
            y2 = parentBounds.y1 + position.bottom!! + size.y
        } else if (position.top != null) {
            y1 = parentBounds.y2 - position.top!! - size.y
            y2 = parentBounds.y2 - position.top!!
        } else {
            y1 = parentBounds.center.y - size.y / 2f
            y2 = parentBounds.center.y + size.y / 2f
        }
    }

    override fun model() = bounds().run {
        Transform().translate(Vector3f(center.x, center.y, 0f)).matrix
    }

    override fun view() = root.view(fixed = true)

    fun text(s: String) = textured(2 * s.length, theme.font.texture, theme.textColor)
}