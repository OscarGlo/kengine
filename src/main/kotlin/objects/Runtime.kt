package objects

import entity.Entity
import entity.Root
import entity.components.Script
import entity.components.render.Render
import objects.gl.Window
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL30.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL30.glClear

class Runtime(private val window: Window) {
    val root = Root(window)

    fun loop() {
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
            glClear(GL_COLOR_BUFFER_BIT)
            root.forEachComponent(Render::render)

            val t0 = t
            t = System.currentTimeMillis()
            root.forEachComponent<Script> { it.update(t - t0, t - start) }

            glfwSwapBuffers(window.id)
            glfwPollEvents()
        }

        glfwTerminate()
    }
}