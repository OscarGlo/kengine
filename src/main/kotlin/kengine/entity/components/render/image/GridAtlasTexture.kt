package kengine.entity.components.render.image

import kengine.math.Color
import kengine.objects.gl.Image
import kengine.util.gridUvs

class GridAtlasTexture(image: Image, rows: Int = 1, cols: Int = 1, color: Color = Color.white) :
    AtlasTexture(image, generateUVs(rows, cols), color) {
    companion object {
        fun generateUVs(rows: Int, cols: Int) =
            (0 until rows).flatMap { y -> (0 until cols).map { x -> gridUvs(rows, cols, x, y) } }
    }
}