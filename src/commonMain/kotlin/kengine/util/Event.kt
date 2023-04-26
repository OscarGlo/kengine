package kengine.util

import kotlin.reflect.KClass

abstract class Event {
    companion object {
        val eventListeners = mutableMapOf<KClass<out Event>, MutableList<(Event) -> Unit>>()
    }

    abstract class Manager {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified E : Event> listener(noinline method: (E) -> Unit) {
            if (!eventListeners.containsKey(E::class))
                eventListeners[E::class] = mutableListOf()
            eventListeners[E::class]?.add(method as (Event) -> Unit)
        }

        protected inline fun <reified E : Event> notify(event: E) =
            eventListeners[E::class]?.forEach { method -> method(event) }
    }
}

