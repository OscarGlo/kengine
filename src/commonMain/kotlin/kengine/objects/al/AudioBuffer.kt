package kengine.objects.al

expect class AudioBuffer() {
    fun init()
    suspend fun store(bufferData: Vorbis)
}