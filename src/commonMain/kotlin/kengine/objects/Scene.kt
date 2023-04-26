package kengine.objects

import kengine.entity.Entity
import kengine.entity.components.physics.Body2D
import kengine.entity.components.render.Camera
import kengine.entity.components.render.Render
import kengine.math.Matrix4
import kengine.math.Vector3f
import kengine.objects.glfw.ResizeEvent
import kengine.util.Event
import kengine.util.doubleTime

class Scene(vararg entities: Entity) : Event.Manager() {
    val root = Entity("")
    var currentCamera: Camera? = null
    var time = 0.0; private set

    init {
        root.add(*entities)
    }

    fun onCameraChange(evt: Camera.SetCurrentEvent) {
        currentCamera = evt.camera
    }

    private lateinit var viewTransform: Matrix4

    fun view(is3D: Boolean = false, fixed: Boolean = false) =
        if (!fixed && currentCamera != null) {
            if (is3D) currentCamera!!.view() else viewTransform * currentCamera!!.view()
        } else viewTransform

    fun setViewScaling(evt: ResizeEvent) {
        viewTransform = Matrix4(scaling = Vector3f(2f / evt.size.x, 2f / evt.size.y, 1f))
    }

    fun add(vararg entities: Entity) = root.add(*entities)

    suspend fun init() {
        listener(this::onCameraChange)
        listener(this::setViewScaling)

        viewTransform = Matrix4(scaling = Vector3f(2f / KERuntime.window.size.x, 2f / KERuntime.window.size.y, 1f))
        root.initAll()
    }

    fun update(t0: Double): Double {
        val t = doubleTime()
        val delta = t - t0

        KERuntime.time += delta
        this.time += delta

        // Updates
        root.forEachRec {
            if (!it.shouldPause())
                it.time += delta
        }
        root.forEachComponentRec<Entity.Component> {
            if (!it.entity.shouldPause()) {
                it.update(delta)

                if (it is Body2D)
                    it.physicsUpdate(delta)
            }
        }

        // Render
        KERuntime.window.clear()
        root.forEachComponentRec(Render::render)

        // Update viewport
        KERuntime.window.update()

        return t
    }
}