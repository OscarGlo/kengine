package kengine.entity.components

import kengine.entity.Entity
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

abstract class Script : Entity.Component() {
    override fun initialize() {
        bindComponents()
        init()
    }

    private fun bindComponents() {
        this::class.memberProperties.filter {
                it.visibility == KVisibility.PUBLIC && it.returnType.isSubtypeOf(Entity.Component::class.starProjectedType)
            }.filterIsInstance<KMutableProperty<*>>()
            .forEach { prop -> entity.getInstance(prop.returnType.jvmErasure)?.let { prop.setter.call(this, it) } }
    }

    open fun init() {}
    open fun update(delta: Double, time: Double) {}
}