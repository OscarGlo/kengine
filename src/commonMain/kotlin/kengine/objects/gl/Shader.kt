package kengine.objects.gl

expect class Shader(vararg steps: Pair<Int, String>) {
    companion object {
        suspend fun init()
        val VERTEX: Int
        val FRAGMENT: Int
    }
    suspend fun init()
    fun use()
    operator fun set(name: String, value: Any)
}