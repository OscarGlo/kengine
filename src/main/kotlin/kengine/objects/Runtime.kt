package kengine.objects

import kengine.entity.Entity
import kengine.entity.Root2D
import kengine.entity.components.Camera2D
import kengine.entity.components.Script
import kengine.entity.components.physics.Body2D
import kengine.entity.components.render.Render
import kengine.math.Matrix4
import kengine.objects.gl.Window
import kengine.util.Event
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.glFlush
import org.lwjgl.opengl.GL30.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL30.glClear

class Runtime(private val window: Window, var vSync: Boolean = true): Event.Manager() {
    companion object {
        private fun doubleTime() = System.nanoTime() / 1_000_000_000.0
    }

    val root = Root2D(window)

    private fun updateCameraPosition() {
        var transform: Matrix4? = null
        root.forEachComponent<Camera2D> {
            if (transform == null && it.current)
                transform = it.transform()
        }
        root.transform.cameraTransform = transform
    }

    // Pass global events to Scripts
    @Event.Listener(eventClass = Event::class)
    fun onEvent(evt: Event) = root.update(evt)

    fun init() {
        window.init()
        window.listeners.add(this)

        root.forEachComponent<Entity.Component> {
            it.root = root
            it.initialize()
        }

        glfwSwapInterval(if (vSync) 1 else 0)
    }

    fun update(t0: Double, start: Double): Double {
        val t = doubleTime()
        val delta = t - t0
        val time = t - start

        // Updates
        root.forEachComponent<Body2D> { it.physicsUpdate(delta) }
        root.forEachComponent<Script> { it.update(delta, time) }

        // Render
        updateCameraPosition()
        glClear(GL_COLOR_BUFFER_BIT)
        root.forEachComponent(Render::render)

        // Update viewport
        glfwSwapBuffers(window.id)
        glfwPollEvents()
        glFlush()

        return t
    }

    fun run() {
        init()

        val start = doubleTime()
        var t = start

        while (!glfwWindowShouldClose(window.id))
            t = update(t, start)

        glfwTerminate()
    }
}