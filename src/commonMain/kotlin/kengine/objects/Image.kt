package kengine.objects

import kengine.math.Vector2i
import kengine.util.Resource

expect abstract class Image(resource: Resource, bpp: Int) {
    val size: Vector2i
}