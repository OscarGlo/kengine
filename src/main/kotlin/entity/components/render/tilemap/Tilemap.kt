package entity.components.render.tilemap

import entity.components.render.ImageRender
import entity.components.render.Render
import objects.gl.Image
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.opengl.GL30.*
import util.rectIndicesN
import util.rectVertices
import util.sizeof

class Tilemap(val size: Vector2f, val tileset: List<Tile>, val tiles: MutableMap<Vector2f, Int>) :
    Render(vertices(size, tileset, tiles), rectIndicesN(tiles.size)) {
    companion object {
        fun vertices(size: Vector2f, tileset: List<Tile>, tiles: MutableMap<Vector2f, Int>) =
            tiles.entries.fold(floatArrayOf()) { acc, e -> acc + rectVertices(size, e.key.mul(size, Vector2f()), tileset[e.value].uvs) }
    }

    class Tile(val image: Image, val uvs: Vector4f, val bitmask: IntArray)

    override val shader = ImageRender.shader

    override fun render() {
        if (!visible) return
        renderBind()
        for ((i, k) in tiles.keys.withIndex()) {
            tileset[tiles[k]!!].image.bind()
            glDrawRangeElements(GL_TRIANGLES, i * 6, i * 6 + 6, 6, GL_UNSIGNED_INT, (i * 6).toLong() * sizeof(GL_UNSIGNED_INT))
        }
    }
}