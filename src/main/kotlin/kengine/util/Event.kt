package kengine.util

import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import kotlin.reflect.full.isSuperclassOf

abstract class Event {
    @Target(AnnotationTarget.FUNCTION)
    annotation class Listener(val eventClass: KClass<out Event>)

    abstract class Manager : Dirtyable() {
        val listeners = mutableListOf<Manager>()

        protected inline fun <reified E : Event> notify(event: E) = listeners.forEach { it.dispatch(event) }

        fun <E : Event> dispatch(event: E) = this::class.functions
            .associateBy { it.annotations.filterIsInstance<Listener>().firstOrNull() }
            .filter { it.key != null && it.key!!.eventClass.isSuperclassOf(event::class) }
            .values.fold(true) { acc, fn ->
                val ret = fn.call(this, event)
                acc && if (ret is Boolean) ret else true
            }
    }
}

