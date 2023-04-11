package kengine.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Dirtyable {
    private var dirty = false

    class SetDirty<T : Any>(private var value: T) : ReadWriteProperty<Dirtyable, T> {
        override fun getValue(thisRef: Dirtyable, property: KProperty<*>) = value

        override fun setValue(thisRef: Dirtyable, property: KProperty<*>, value: T) {
            this.value = value
            thisRef.dirty = true
        }
    }

    class GetDirty<T : Any>(private var value: T, private val get: () -> T) : ReadWriteProperty<Dirtyable, T> {
        override fun getValue(thisRef: Dirtyable, property: KProperty<*>): T {
            if (thisRef.dirty)
                value = get()
            return value
        }

        override fun setValue(thisRef: Dirtyable, property: KProperty<*>, value: T) {
            thisRef.dirty = false
            this.value = value
        }
    }
}