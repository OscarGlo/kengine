package kengine.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Initable {
    class RequiresInit<V>(initable: Initable, val default: V, private val setter: (value: V) -> Unit) :
        ReadWriteProperty<Initable, V> {
        private var cache = default

        init {
            initable.initableProperties.add(this)
        }

        fun init() {
            if (cache != default) setter(cache)
        }

        override fun getValue(thisRef: Initable, property: KProperty<*>) = cache

        override fun setValue(thisRef: Initable, property: KProperty<*>, value: V) {
            if (thisRef.isInit) setter(value)
            cache = value
        }
    }

    var isInit: Boolean
    val initableProperties: MutableList<RequiresInit<*>>
}