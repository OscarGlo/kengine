package entity

import util.terminateError
import kotlin.reflect.KClass

open class Entity(private val id: String) {
    abstract class Component {
        lateinit var entity: Entity

        fun attach(e: Entity) = apply { entity = e }

        open fun initialize() {}
    }

    private val children = mutableMapOf<String, Entity>()
    var parent: Entity? = null
    val components = mutableListOf<Component>()

    init {
        parent?.children?.set(id, this)
    }

    fun children(vararg entities: Entity) = apply {
        entities.forEach {
            it.parent = this
            children[it.id] = it
        }
    }

    fun forEach(fn: (Entity) -> Unit) {
        fn(this)
        children.values.forEach { it.forEach(fn) }
    }

    inline fun <reified T : Component> forEachComponent(crossinline fn: (T) -> Unit) = forEach {
        it.getAll<T>().forEach { fn(it) }
    }

    fun path(): String = if (parent != null) parent!!.path() + "/" + id else id

    operator fun get(id: String) = children[id] ?: terminateError("Entity ${path()} has no child with id $id")

    // Components
    fun with(component: Component) = apply { components.add(component.attach(this)) }

    inline fun <reified T : Component> has() = components.any { T::class.isInstance(it) }

    inline fun <reified T : Component> getAll() = components.filterIsInstance<T>()

    inline fun <reified T : Component> getOrNull() = getAll<T>().firstOrNull()

    inline fun <reified T : Component> get() =
        getOrNull<T>() ?: terminateError("${T::class.simpleName} component not found on entity ${path()}")

    @Suppress("UNCHECKED_CAST")
    fun getInstance(clazz: KClass<*>) = components.firstOrNull { clazz.isInstance(it) }
}