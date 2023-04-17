package kengine.entity.components

import kengine.entity.Entity
import kengine.math.Matrix4
import kengine.math.Quaternion
import kengine.math.Vector3f

open class Transform(val is3D: Boolean = false, val fixed: Boolean = false) : Entity.Component() {
    val matrix by GetDirty(Matrix4()) { Matrix4(position, scaling, rotation)  }

    var position by SetDirty(Vector3f())
    var scaling by SetDirty(Vector3f(1f))
    var rotation by SetDirty(Quaternion())

    fun global(): Matrix4 =
        if (entity.parent != null && entity.parent!!.has<Transform>())
            matrix * entity.parent!!.get<Transform>().global()
        else matrix

    fun translate(v: Vector3f) = apply { position += v }
    fun scale(v: Vector3f) = apply { scaling *= v }
    fun rotate(q: Quaternion) = apply { rotation *= q }
}