package kengine.entity.components.render

import kengine.entity.components.render.image.ImageRender
import kengine.math.Rect
import kengine.math.Vector2f
import kengine.math.Vector2i
import kengine.objects.gl.Image
import kengine.util.gridUvs
import kengine.util.rectIndicesN
import kengine.util.rectVertices
import kengine.util.sizeof
import org.lwjgl.opengl.GL30.*
import kotlin.collections.Map.Entry
import kotlin.math.pow

typealias Axes = Pair<Vector2f, Vector2f>

class Tilemap(
    val size: Vector2f,
    private val tileset: List<Tile>,
    val tiles: MutableMap<Vector2i, Ref>,
    val axes: Axes = rectAxes
) : Render(vertices(size, axes, tileset, mapOf()), rectIndicesN(tiles.size)) {
    companion object {
        val rectAxes = Vector2f(1f, 0f) to Vector2f(0f, 1f)
        val isoAxes = Vector2f(0.5f, -0.5f) to Vector2f(0.5f, 0.5f)

        const val NONE = -1
        const val ANY = -2
        const val OTHER = -3

        private val edges = listOf(4, 6, 14, 12, 5, 7, 15, 13, 1, 3, 11, 9, 0, 2, 10, 8)

        fun edgeTileset(image: Image, id: Int) = edges.mapIndexed { i, e ->
            Tile(
                image,
                gridUvs(4, 4, i % 4, i / 4),
                intArrayOf(
                    ANY, if (e and 4 > 0) id else OTHER, ANY,
                    if (e and 8 > 0) id else OTHER, id, if (e and 2 > 0) id else OTHER,
                    ANY, if (e and 1 > 0) id else OTHER, ANY
                )
            )
        }

        private val corners = listOf(4, 3, 14, 6, 10, 7, 15, 13, 1, 9, 11, 12, 0, 2, 5, 8)

        fun cornerTileset(image: Image, id: Int) = corners.mapIndexed { i, corner ->
            val c = (0..3).map { j -> corner and 2.0.pow(j).toInt() > 0 }
            Tile(
                image,
                gridUvs(4, 4, i % 4, i / 4),
                intArrayOf(
                    if (c[2]) id else ANY, if (c[1] || c[2]) id else OTHER, if (c[1]) id else ANY,
                    if (c[2] || c[3]) id else OTHER, id, if (c[0] || c[1]) id else OTHER,
                    if (c[3]) id else ANY, if (c[3] || c[0]) id else OTHER, if (c[0]) id else ANY
                )
            )
        }

        private val full = listOf(
            16, 20, 84, 80, 213, 92, 116, 87, 28, 125, 124, 112,
            17, 21, 85, 81, 29, 127, 253, 113, 31, 119, -1, 245,
            1, 5, 69, 65, 23, 223, 247, 209, 95, 255, 221, 241,
            0, 4, 68, 64, 117, 71, 197, 93, 7, 199, 84, 193
        )

        fun fullTileset(image: Image, id: Int) = full.mapIndexed { i, mask ->
            val m = (0..8).map { j -> mask and 2.0.pow(j).toInt() > 0 }
            fun fullCorner(i: Int) = if (m[i]) id else if (m[(i - 1) % 8] && m[(i + 1) % 8]) OTHER else ANY
            Tile(
                image,
                gridUvs(4, 12, i % 12, i / 12),
                intArrayOf(
                    fullCorner(5), if (m[4]) id else OTHER, fullCorner(3),
                    if (m[6]) id else OTHER, id, if (m[2]) id else OTHER,
                    fullCorner(7), if (m[0]) id else OTHER, fullCorner(1)
                )
            )
        }

        private val vectorComparator = compareBy<Vector2i> { it.y }.thenBy { it.x }
        private val mapComparator: Comparator<Entry<Vector2i, Int>> = compareBy(vectorComparator) { it.key }

        private fun vertices(size: Vector2f, axes: Axes, tileset: List<Tile>, tileIds: Map<Vector2i, Int>) =
            tileIds.entries.sortedWith(mapComparator).fold(floatArrayOf()) { acc, (pos, id) ->
                val offset = (axes.first * pos.x.toFloat() + axes.second * pos.y.toFloat()) * size
                acc + rectVertices(size, offset, tileset[id].uv)
            }
    }

    class Ref(val id: Int, val auto: Boolean = false)

    class Tile(val image: Image, val uv: Rect = Rect(0f, 0f, 1f, 1f), val bitmask: IntArray? = null) {
        val type
            get() = bitmask?.getOrNull(4) ?: 0
    }

    override fun getShader() = ImageRender.shader

    private val tileIds = mutableMapOf<Vector2i, Int>()
    private val bitmaskCache = mutableMapOf<Vector2i, IntArray>()
    private var updateBuffer = false

    override fun initialize() {
        super.initialize()

        tileset.forEach { tile -> tile.image.init() }

        tiles.forEach { (pos, ref) -> this[pos] = ref }
        updateBuffers()
        updateBuffer = true
    }

    private fun type(ref: Ref) = if (ref.auto) ref.id else tileset[ref.id].type

    operator fun set(pos: Vector2i, ref: Ref) {
        tiles[pos] = ref
        if (!ref.auto)
            tileIds[pos] = ref.id
        else // Update all neighboring tiles
            for (x in (pos.x - 1)..(pos.x + 1))
                for (y in (pos.y - 1)..(pos.y + 1))
                    update(Vector2i(x, y))

        if (updateBuffer) updateBuffers()
    }

    private fun updateBuffers() {
        arrayBuffer.store(vertices(size, axes, tileset, tileIds))
        elementBuffer.store(rectIndicesN(tileIds.size))
    }

    private fun update(pos: Vector2i) {
        if (pos !in tiles || !tiles[pos]!!.auto) return

        val bitmask = ((pos.y - 1)..(pos.y + 1)).flatMap { y ->
            ((pos.x - 1)..(pos.x + 1)).map { x ->
                val t = tiles[Vector2i(x, y)]
                if (t == null) NONE else type(t)
            }
        }.toIntArray()

        // Ignore useless updates
        if (bitmask.contentEquals(bitmaskCache[pos]))
            return
        bitmaskCache[pos] = bitmask

        // Pick random tile of type with matching bitmask, or default to first tile of type
        val typeTiles = tileset.withIndex().filter { it.value.type == tiles[pos]!!.id }
        val validTiles = typeTiles.filter {
            it.value.bitmask != null && it.value.bitmask!!.matches(bitmask, tiles[pos]!!.id)
        }
        tileIds[pos] = validTiles.randomOrNull()?.index ?: typeTiles.getOrNull(0)?.index ?: 0
    }

    private fun IntArray.matches(b: IntArray, type: Int) = foldIndexed(true) { i, match, id ->
        val i2 = b[i]
        match && (id == i2 || id == ANY || i2 == ANY || (id == OTHER && i2 != type) || (i2 == OTHER && id != type))
    }

    override fun render() {
        if (!visible) return
        renderBind()
        for ((i, pos) in tileIds.keys.sortedWith(vectorComparator).withIndex()) {
            tileset[tileIds[pos]!!].image.bind()
            glDrawRangeElements(
                GL_TRIANGLES,
                i * 6,
                i * 6 + 6,
                6,
                GL_UNSIGNED_INT,
                (i * 6).toLong() * sizeof(GL_UNSIGNED_INT)
            )
        }
    }
}