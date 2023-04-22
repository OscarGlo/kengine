package kengine.util

import java.net.URL
import java.nio.file.Paths

actual class Resource actual constructor(actual val path: String, local: Boolean) {
    val url: URL = if (local)
        URL("file:/${Paths.get(System.getProperty("user.dir"), path)}")
    else
        Resource::class.java.getResource(path) ?: terminateError("Couldn't find global resource at $path")

    actual suspend fun waitForLoad() {}

    actual suspend fun getText() = url.readText()
}