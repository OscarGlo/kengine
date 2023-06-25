package kengine.objects

import kengine.objects.gl.Shader
import kengine.objects.glfw.Window
import kengine.tools.Debug
import kengine.tools.Event
import org.lwjgl.glfw.GLFW.*

class KERuntime private constructor() : Event.Manager() {
    companion object {
        private val instance = KERuntime()

        var window
            get() = instance.window
            set(w) {
                instance.window = w
            }

        var scene
            get() = instance.scene
            set(s) {
                if (instance.isInit) s.init()
                instance.scene = s
            }

        val root get() = instance.scene.root

        fun run() = instance.run()

        var time: Double = 0.0
        fun doubleTime() = System.nanoTime() / 1_000_000_000.0
    }

    lateinit var window: Window
    lateinit var scene: Scene

    var vSync = true

    private var isInit = false

    fun init() {
        if (isInit) return

        window.init()

        Shader.init()
        Debug.init()
        scene.init()

        glfwSwapInterval(if (vSync) 1 else 0)

        isInit = true
    }

    fun run() {
        init()

        var t = doubleTime()
        while (!glfwWindowShouldClose(window.id))
            t = scene.update(t)

        glfwTerminate()
    }
}