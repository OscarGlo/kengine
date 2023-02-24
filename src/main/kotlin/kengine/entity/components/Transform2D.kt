package kengine.entity.components

import kengine.entity.Entity
import kengine.math.Matrix4
import kengine.util.terminateError

open class Transform2D(var fixed: Boolean = false) : Entity.Component() {
    val matrix = Matrix4()

    fun global(): Matrix4 =
        if (entity.parent != null && entity.has<Transform2D>())
            matrix * entity.parent!!.get<Transform2D>().global()
        else matrix

    open fun rootViewport(fixed: Boolean = false): Matrix4 =
        entity.parent?.get<Transform2D>()?.rootViewport(this.fixed || fixed)
            ?: terminateError("No root transform for entity ${entity.path()}")

    fun viewport() = rootViewport(fixed) * global()
}