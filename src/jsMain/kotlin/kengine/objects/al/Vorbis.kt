package kengine.objects.al

import kengine.util.Resource
import kengine.util.arrayBuffer
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer

actual class Vorbis actual constructor(val resource: Resource) {
    lateinit var arrayBuffer: ArrayBuffer

    init {
        suspend {
            arrayBuffer = resource.getBlob().arrayBuffer().await()
        }
    }
}