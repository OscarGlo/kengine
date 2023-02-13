package kengine.objects.util

import java.net.URL
import java.nio.file.Paths

object Resource {
    var localPath: String = ""

    fun global(path: String, error: String = "Global resource not found at path $path") =
        Resource::class.java.getResource(path) ?: terminateError(error)

    fun local(path: String) =
        URL("file:/${Paths.get(System.getProperty("user.dir"), localPath, path)}")
}