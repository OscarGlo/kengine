package kengine.entity

import kengine.util.Event
import kengine.util.terminateError
import kotlin.reflect.KClass

open class Entity(val id: String, vararg components: Component) : Event.Manager() {
    abstract class Component : Event.Manager() {
        lateinit var entity: Entity
        lateinit var root: Root2D

        open val required: List<KClass<out Component>> = emptyList()
        open val incompatible: List<KClass<out Component>> = emptyList()

        fun checkCompatibility() {
            val requiredMap = required.associateWith { false }.toMutableMap()

            for (component in entity.components) {
                if (component::class in incompatible)
                    terminateError("Incompatible component types ${component::class.simpleName} and ${this::class.simpleName} on entity ${entity.path()}")

                if (requiredMap.containsKey(component::class))
                    requiredMap[component::class] = true
            }

            for (component in requiredMap.keys) {
                if (requiredMap[component] == false)
                    terminateError("Missing required component ${component.simpleName} for ${this::class.simpleName} on entity ${entity.path()}")
            }
        }

        fun attach(e: Entity) = apply { entity = e }

        open fun initialize() {}
        open fun update(delta: Double, time: Double) {}
    }

    private val children = mutableMapOf<String, Entity>()
    var parent: Entity? = null
    val components = components.toMutableList().onEach { it.entity = this }

    init {
        parent?.children?.set(id, this)
    }

    @Event.Listener(Event::class)
    fun onEvent(evt: Event) {
        var cont = true
        forEachComponentRec<Component> {
            if (cont && !it.update(evt))
                cont = false
        }
    }

    fun children(vararg entities: Entity) = apply {
        entities.forEach { add(it) }
    }

    fun add(entity: Entity) {
        entity.parent = this
        children[entity.id] = entity
    }

    fun forEachRec(fn: (Entity) -> Unit) {
        fn(this)
        children.values.forEach { it.forEachRec(fn) }
    }

    fun <R> map(fn: (Entity) -> R): List<R> = listOf(fn(this)) + children.values.flatMap { it.map(fn) }

    inline fun <reified T : Component> forEachComponentRec(crossinline fn: (T) -> Unit) = forEachRec {
        it.getAll<T>().forEach(fn)
    }

    inline fun <reified T : Component, R> mapComponentsRec(crossinline fn: (T) -> R) = map {
        it.getAll<T>().map(fn)
    }.flatten()

    fun path(): String = if (parent != null) parent!!.path() + "/" + id else id

    operator fun get(id: String) = children[id] ?: terminateError("Entity ${path()} has no child with id $id")

    // Components
    fun add(component: Component) = apply { components.add(component.attach(this)) }

    inline fun <reified T : Component> has() = components.any { T::class.isInstance(it) }

    inline fun <reified T : Component> getAll() = components.filterIsInstance<T>()

    inline fun <reified T : Component> getOrNull() = getAll<T>().firstOrNull()

    inline fun <reified T : Component> get() =
        getOrNull<T>() ?: terminateError("${T::class.simpleName} component not found on entity ${path()}")

    @Suppress("UNCHECKED_CAST")
    fun getInstance(clazz: KClass<*>) = components.firstOrNull { clazz.isInstance(it) }
}