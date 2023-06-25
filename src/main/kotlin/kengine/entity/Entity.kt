package kengine.entity

import kengine.tools.Event
import kengine.util.terminateError
import kotlin.reflect.KClass

// TODO: Auto id
open class Entity(val id: String, vararg components: Component) : Event.Manager() {
    abstract class Component : Event.Manager() {
        lateinit var entity: Entity

        open val required: List<KClass<out Component>> = emptyList()
        open val incompatible: List<KClass<out Component>> = emptyList()

        fun checkCompatibility() {
            val componentClasses = entity.components.map { it::class }

            val incomp = componentClasses.filter { it in incompatible }
            if (incomp.isNotEmpty())
                terminateError("Incompatible component(s) ${incomp.joinToString { it.simpleName ?: "?" }} with ${this::class.simpleName} on entity ${entity.path()}")

            val missing = required.filter { it !in componentClasses }
            if (missing.isNotEmpty())
                terminateError("Missing required component(s) ${missing.joinToString { it.simpleName ?: "?" }} for ${this::class.simpleName} on entity ${entity.path()}")
        }

        fun attach(e: Entity) = apply {
            entity = e
            checkCompatibility()
        }

        open fun initialize() {}
        open fun update(delta: Double) {}
    }

    class TogglePause(val paused: Boolean) : Event()

    enum class PauseMode {
        Inherit, Pause, Ignore
    }

    private val children = mutableMapOf<String, Entity>()
    var parent: Entity? = null
    val components = components.toMutableList().onEach { it.entity = this }

    init {
        parent?.children?.set(id, this)
    }

    var time: Double = 0.0

    var pauseMode: PauseMode = PauseMode.Inherit
    var paused: Boolean = false
        set(p) {
            if (p == field) return

            field = p
            notify(TogglePause(p))
            children.forEach { (_, e) -> e.paused = p }
        }

    private fun canPause(): Boolean =
        pauseMode == PauseMode.Pause || (pauseMode == PauseMode.Inherit && (parent == null || parent!!.canPause()))

    fun shouldPause() = paused && canPause()

    fun add(vararg entities: Entity) = apply {
        entities.forEach {
            it.parent = this
            children[it.id] = it
        }
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

    fun getInstance(clazz: KClass<*>) = components.firstOrNull { clazz.isInstance(it) }
}