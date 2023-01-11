package entity.components

import entity.Entity
import objects.al.Source

class AudioPlayer(val relative: Boolean = false) : Entity.Component() {
    val source = Source()
}