package entity.components.render

import objects.gl.Image
import org.joml.Vector4f
import util.gridUvs
import util.white

class GridAtlasTexture(image: Image, rows: Int = 1, cols: Int = 1, color: Vector4f = white) :
    AtlasTexture(image, generateUVs(rows, cols), color) {
    companion object {
        fun generateUVs(rows: Int, cols: Int) =
            (0 until rows).flatMap { y -> (0 until cols).map { x -> gridUvs(rows, cols, x, y) } }
    }
}