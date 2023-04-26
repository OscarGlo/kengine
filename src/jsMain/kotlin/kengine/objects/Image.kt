package kengine.objects

import kengine.math.Vector2i
import kengine.util.Resource
import kotlinx.browser.window
import org.w3c.files.Blob

actual abstract class Image actual constructor(val resource: Resource, bpp: Int) {
    lateinit var blob: Blob
    actual val size = Vector2i()

    init {
        suspend {
            blob = resource.getBlob()
            window.createImageBitmap(blob).then {
                size.x = it.width
                size.y = it.height
            }
        }
    }
}