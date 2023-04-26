package kengine.util

expect class Resource(path: String, local: Boolean = true) {
    val path: String
    val local: Boolean
    suspend fun waitForLoad()
    suspend fun getText(): String
}