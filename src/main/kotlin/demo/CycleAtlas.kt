package demo

import entity.components.render.AtlasTexture
import entity.components.Script

class CycleAtlas : Script() {
    lateinit var atlas: AtlasTexture
    private var t = 0L

    override fun update(delta: Long, time: Long) {
        if (t != time / 400) {
            atlas.frame = (atlas.frame + 1) % atlas.frameCount
            t = time / 400
        }
    }
}