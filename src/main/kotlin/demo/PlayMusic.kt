package demo

import entity.components.AudioPlayer
import entity.components.Script
import objects.al.Audio
import objects.al.Vorbis

class PlayMusic : Script() {
    lateinit var player: AudioPlayer
    private val music = Audio()

    override fun init() {
        music.store(Vorbis("/sounds/audio.ogg"))
        player.source.loop = true
        player.source
            .load(music)
            .play()
    }
}
