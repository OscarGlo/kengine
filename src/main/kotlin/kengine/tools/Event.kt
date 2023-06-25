package kengine.tools

import kengine.util.Dirtyable
import java.lang.reflect.Method

typealias EventClass = Class<*>
typealias ListenerClass = Class<*>

abstract class Event {
    companion object {
        private val classEvents = mutableMapOf<ListenerClass, List<EventClass>>()
        val eventMethods = mutableMapOf<EventClass, MutableMap<ListenerClass, MutableList<Method>>>()
        val eventListeners = mutableMapOf<EventClass, MutableList<Any>>()

        fun registerClass(clazz: ListenerClass) {
            val typeMethods = clazz.methods
                .filter { it.annotations.filterIsInstance<Listener>().isNotEmpty() }
                .map { it.parameterTypes[0]!! to it }

            classEvents[clazz] = typeMethods.map { it.first }

            typeMethods.forEach { (kclass, method) ->
                eventMethods.getOrPut(kclass) { mutableMapOf() }.getOrPut(clazz) { mutableListOf() }.add(method)
            }
        }

        fun register(manager: Manager) {
            if (manager.javaClass !in classEvents)
                registerClass(manager.javaClass)

            classEvents[manager::class.java]?.forEach {
                eventListeners.getOrPut(it) { mutableListOf() }.add(manager)
            }
        }
    }

    @Target(AnnotationTarget.FUNCTION)
    annotation class Listener

    abstract class Manager : Dirtyable() {
        init {
            register(this)
        }

        protected inline fun <reified E : Event> notify(event: E) = eventListeners[E::class.java]?.forEach { listener ->
            eventMethods[E::class.java]?.get(listener::class.java)?.forEach {
                it.invoke(listener, event)
            }
        }
    }
}

