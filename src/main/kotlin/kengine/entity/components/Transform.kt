package kengine.entity.components

import kengine.entity.Entity
import kengine.math.Matrix4
import kengine.math.Quaternion
import kengine.math.Vector3f

open class Transform(val is3D: Boolean = false, val fixed: Boolean = false) : Entity.Component() {
    val matrix by GetDirty(Matrix4()) {
        val rot = rotation.matrix()
        Matrix4(
            mutableListOf(
                mutableListOf(scaling.x * rot[0, 0], rot[1, 0], rot[2, 0], position.x),
                mutableListOf(rot[0, 1], scaling.y * rot[1, 1], rot[2, 1], position.y),
                mutableListOf(rot[0, 2], rot[1, 2], scaling.z * rot[2, 2], position.z),
                mutableListOf(0f, 0f, 0f, 1f)
            )
        )
    }

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