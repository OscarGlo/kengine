package kengine.entity.components

import kengine.entity.Entity
import kengine.math.Matrix3
import kengine.math.Matrix4
import kengine.math.Vector3f

class Camera2D(
    var current: Boolean = false,
    private val keepScaling: Boolean = false,
    private val customTransform: Matrix4 = Matrix4(),
) : Entity.Component() {
    fun transform(): Matrix4 {
        val entityTransform = entity.get<Transform2D>().global()
        val baseTransform = entityTransform.apply {
            scaling = if (!keepScaling) Vector3f(1f) else scaling.inverse()
            rotation = Matrix3()
            position = scaling * -position
        }
        return baseTransform * customTransform
    }
}