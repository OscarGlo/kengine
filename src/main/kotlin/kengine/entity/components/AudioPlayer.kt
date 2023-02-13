package kengine.entity.components

import kengine.entity.Entity
import kengine.objects.al.Source

class AudioPlayer(val relative: Boolean = false) : Entity.Component() {
    val source = Source()
}