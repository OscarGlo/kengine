package util

import java.net.URL
import java.nio.file.Paths

object Resource {
    var localPath: String? = null

    fun global(path: String, error: String = "Global resource not found at path $path") =
        Resource::class.java.getResource(path) ?: terminateError(error)

    fun local(path: String) =
        if (localPath != null) URL(Paths.get(localPath!!, "").toString())
        else global(path, "Local resource not found at path $path")
}