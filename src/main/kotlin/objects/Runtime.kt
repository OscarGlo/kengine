package objects

import entity.Entity
import entity.Root2D
import entity.components.Camera2D
import entity.components.Script
import entity.components.physics.Collider2D
import entity.components.render.Render
import objects.gl.Window
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL30.glClear

class Runtime(private val window: Window) {
    val root = Root2D(window)

    private fun updateCameraPosition() {
        var transform: Matrix4f? = null
        root.forEachComponent<Camera2D> {
            if (transform == null && it.current)
                transform = it.transform()
        }
        root.transform.cameraTransform = transform
    }

    fun loop() {
        // Init script listeners
        window.resizeListeners.add { width, height ->
            root.forEachComponent<Script> { it.onResize(width, height) }
        }
        window.keyListeners.add { key, scancode, action, mods ->
            root.forEachComponent<Script> { it.onKey(key, scancode, action, mods) }
        }
        window.mouseButtonListeners.add { button, action, mods ->
            root.forEachComponent<Script> { it.onMouseButton(button, action, mods) }
        }
        window.mouseMoveListeners.add { x, y ->
            root.forEachComponent<Script> { it.onMouseMove(x, y) }
        }

        root.forEachComponent(Entity.Component::initialize)

        val start = System.currentTimeMillis()
        var t = start

        while (!glfwWindowShouldClose(window.id)) {
            updateCameraPosition()

            // Render
            glClear(GL_COLOR_BUFFER_BIT)
            root.forEachComponent(Render::render)

            // Calculate collisions
            root.forEachComponent<Collider2D> { a ->
                a.collisions.clear()
                root.forEachComponent<Collider2D> { b ->
                    if (a != b && a.collide(b))
                        a.collisions.add(b.entity)
                }
            }

            // Run script updates
            val t0 = t
            t = System.currentTimeMillis()
            root.forEachComponent<Script> { it.update(t - t0, t - start) }

            glfwSwapBuffers(window.id)
            glfwPollEvents()
        }

        glfwTerminate()
    }
}