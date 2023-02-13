package kengine.entity.components.render.tilemap

import kengine.entity.components.render.ImageRender
import kengine.entity.components.render.Render
import kengine.objects.gl.Image
import kengine.objects.util.*
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f
import org.lwjgl.opengl.GL30.*
import kotlin.collections.Map.Entry

class Tilemap(val size: Vector2f, private val tileset: List<Tile>, val tiles: MutableMap<Vector2i, Ref>) :
    Render(vertices(size, tileset, mapOf()), rectIndicesN(tiles.size)) {
    companion object {
        const val NONE = -1
        const val ANY = -2
        const val OTHER = -3

        fun edgeTileset(image: Image, id: Int) = (0..15).map {
            Tile(
                image,
                gridUvs(4, 4, it % 4, it.floorDiv(4)),
                intArrayOf(
                    ANY, if (it < 8) id else OTHER, ANY,
                    if (it % 4 > 1) id else OTHER, id, if (it % 4 in 1..2) id else OTHER,
                    ANY, if (it in 4..11) id else OTHER, ANY
                )
            )
        }

        private val corners = listOf(
            listOf(1, 3, 7, 8, 9, 11, 13, 14),
            listOf(0, 2, 3, 8, 9, 10, 11, 12),
            listOf(5, 6, 7, 9, 10, 11, 12, 13),
            listOf(2, 4, 6, 8, 11, 12, 13, 14)
        )

        fun cornerTileset(image: Image, id: Int) = (0..15).map {
            val c = corners.map { corner -> corner.contains(it) }
            Tile(
                image,
                gridUvs(4, 4, it % 4, it.floorDiv(4)),
                intArrayOf(
                    if (c[0]) id else ANY, if (c[0] || c[1]) id else OTHER, if (c[1]) id else ANY,
                    if (c[0] || c[2]) id else OTHER, id, if (c[1] || c[3]) id else OTHER,
                    if (c[2]) id else ANY, if (c[2] || c[3]) id else OTHER, if (c[3]) id else ANY
                )
            )
        }

        private val vectorComparator = compareBy<Vector2i> { it.y }.thenBy { it.x }
        private val mapComparator: Comparator<Entry<Vector2i, Int>> = compareBy(vectorComparator) { it.key }

        private fun vertices(size: Vector2f, tileset: List<Tile>, tileIds: Map<Vector2i, Int>) =
            tileIds.entries.sortedWith(mapComparator).fold(floatArrayOf()) { acc, (pos, id) ->
                acc + rectVertices(size, pos.f().mul(size, Vector2f()), tileset[id].uvs)
            }
    }

    class Ref(val id: Int, val auto: Boolean = false)

    class Tile(val image: Image, val uvs: Vector4f, val bitmask: IntArray? = null) {
        val type
            get() = bitmask?.getOrNull(4) ?: 0
    }

    override val shader = ImageRender.shader

    private val tileIds = mutableMapOf<Vector2i, Int>()
    private val bitmaskCache = mutableMapOf<Vector2i, IntArray>()
    private var updateBuffer = false

    init {
        tiles.forEach { (pos, ref) -> this[pos] = ref }
        updateBuffers()
        updateBuffer = true
    }

    private fun type(ref: Ref) = if (ref.auto) ref.id else tileset[ref.id].type

    operator fun set(pos: Vector2i, ref: Ref) {
        tiles[pos] = ref
        if (!ref.auto)
            tileIds[pos] = ref.id
        else
            // Update all neighboring tiles
            for (x in (pos.x - 1)..(pos.x + 1))
                for (y in (pos.y - 1)..(pos.y + 1))
                    update(Vector2i(x, y))

        if (updateBuffer) updateBuffers()
    }

    private fun updateBuffers() {
        arrayBuffer.store(vertices(size, tileset, tileIds))
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
            glDrawRangeElements(GL_TRIANGLES, i * 6, i * 6 + 6, 6, GL_UNSIGNED_INT, (i * 6).toLong() * sizeof(GL_UNSIGNED_INT))
        }
    }
}