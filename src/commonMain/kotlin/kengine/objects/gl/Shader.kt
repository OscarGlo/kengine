package kengine.objects.gl

import kengine.util.Resource

expect class Shader(vararg steps: Pair<Int, Resource>) {
    companion object {
        suspend fun init()
        val VERTEX: Int
        val FRAGMENT: Int
    }
    suspend fun init()
    fun use()
    operator fun set(name: String, value: Any)
}