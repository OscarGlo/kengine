package kengine.objects

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.entity.components.physics.Body2D
import kengine.entity.components.render.Camera
import kengine.entity.components.render.Render
import kengine.math.Vector3f
import kengine.objects.glfw.Window
import kengine.util.Event
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.*

class Scene(vararg entities: Entity) : Event.Manager() {
    val root = Entity("")
    var currentCamera: Camera? = null
    var time = 0.0; private set

    @Event.Listener(Camera.SetCurrentEvent::class)
    fun onCameraChange(evt: Camera.SetCurrentEvent) {
        currentCamera = evt.camera
    }

    private val viewTransform = Transform()

    fun view(is3D: Boolean = false, fixed: Boolean = false) =
        if (!fixed && currentCamera != null) {
            if (is3D) currentCamera!!.view() else viewTransform.matrix * currentCamera!!.view()
        } else viewTransform.matrix

    init {
        viewTransform.scale(Vector3f(2f / KERuntime.window.size.x, 2f / KERuntime.window.size.y, 1f))
        root.add(*entities)
    }

    @Event.Listener(Window.ResizeEvent::class)
    fun setViewScaling(evt: Window.ResizeEvent) {
        viewTransform.scaling = Vector3f(2f / evt.size.x, 2f / evt.size.y, 1f)
    }

    fun add(vararg entities: Entity) = root.add(*entities)

    fun init() = root.forEachComponentRec<Entity.Component> {
        it.root = root
        it.initialize()
        it.checkCompatibility()
    }

    fun update(t0: Double): Double {
        val t = KERuntime.doubleTime()
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
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        root.forEachComponentRec(Render::render)

        // Update viewport
        glfwSwapBuffers(KERuntime.window.id)
        glfwPollEvents()
        glFlush()

        return t
    }
}