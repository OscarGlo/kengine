package entity.components

import entity.Entity
import org.joml.Matrix4f
import org.joml.Vector3f
import util.times

class Camera2D(
    var current: Boolean = false,
    private val customTransform: Matrix4f = Matrix4f(),
) : Entity.Component() {
    fun transform() = entity.get<Transform2D>().global().run {
        val pos = transformPosition(Vector3f())
        // Lock rotation and scale
        // TODO: Allow disabling this
        setRotationXYZ(0f, 0f, 0f)
        setTranslation(-pos.x, -pos.y, 0f)
    } * customTransform
}