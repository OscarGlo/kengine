package kengine.util

import java.lang.reflect.Method
import kotlin.reflect.KClass

typealias EventClass = KClass<out Event>
typealias ListenerClass = Class<*>

abstract class Event {
    companion object {
        private val classEvents = mutableMapOf<ListenerClass, List<EventClass>>()
        val eventMethods = mutableMapOf<EventClass, MutableMap<ListenerClass, MutableList<Method>>>()
        val eventListeners = mutableMapOf<EventClass, MutableList<Any>>()

        fun registerClass(clazz: ListenerClass) {
            val typeMethods = clazz.methods
                .associateBy { it.annotations.filterIsInstance<Listener>().firstOrNull() }
                .filter { (annotation, _) -> annotation != null }
                .map { (annotation, method) -> annotation!!.eventClass to method }

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
    annotation class Listener(val eventClass: KClass<out Event>)

    abstract class Manager : Dirtyable() {
        init {
            register(this)
        }

        protected inline fun <reified E : Event> notify(event: E) = eventListeners[E::class]?.forEach { listener ->
            eventMethods[E::class]?.get(listener::class.java)?.forEach {
                it.invoke(listener, event)
            }
        }
    }
}

