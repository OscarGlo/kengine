package entity.components

import entity.Entity
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

abstract class Script : Entity.Component() {
    override fun initialize() {
        // Bind properties
        this::class.memberProperties
            .filter {
                it.visibility == KVisibility.PUBLIC
                        && it.returnType.isSubtypeOf(Entity.Component::class.starProjectedType)
            }
            .filterIsInstance<KMutableProperty<*>>()
            .forEach { prop -> entity.getInstance(prop.returnType.jvmErasure)?.let { prop.setter.call(this, it) } }

        init()
    }

    open fun init() {}
    open fun update(delta: Long, time: Long) {}

    // Event functions
    open fun onResize(width: Int, height: Int) {}
    open fun onKey(key: Int, scancode: Int, action: Int, mods: Int) {}
    open fun onMouseButton(button: Int, action: Int, mods: Int) {}
    open fun onMouseMove(x: Double, y: Double) {}
}