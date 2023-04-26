package kengine.util

import kengine.util.Settings.localPath
import java.net.URL
import java.nio.file.Paths

actual class Resource actual constructor(actual val path: String, actual val local: Boolean) {
    val url: URL = if (local)
        URL("file:/${Paths.get(System.getProperty("user.dir"), localPath, path)}")
    else
        Resource::class.java.getResource(path) ?: terminateError("Couldn't find global resource at $path")

    actual suspend fun waitForLoad() {}

    actual suspend fun getText() = url.readText()

    override fun equals(other: Any?) = this === other || other is Resource && local == other.local && path == other.path

    override fun hashCode() = path.hashCode() + local.hashCode()
}