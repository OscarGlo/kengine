package kengine.objects.gl

import kengine.math.Vector2i
import kengine.util.Resource

expect class GLImage(resource: Resource, bpp: Int = 4, filter: Boolean = true) {
    val size: Vector2i
    suspend fun init()
    fun bind()
}