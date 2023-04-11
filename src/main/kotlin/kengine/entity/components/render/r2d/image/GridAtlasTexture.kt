package kengine.entity.components.render.r2d.image

import kengine.objects.gl.GLImage
import kengine.util.gridUvs

class GridAtlasTexture(image: GLImage, rows: Int = 1, cols: Int = 1) :
    AtlasTexture(image, generateUVs(rows, cols)) {
    companion object {
        fun generateUVs(rows: Int, cols: Int) =
            (0 until rows).flatMap { y -> (0 until cols).map { x -> gridUvs(rows, cols, x, y) } }
    }
}