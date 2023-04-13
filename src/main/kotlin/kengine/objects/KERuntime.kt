package kengine.objects

import kengine.objects.gl.Shader
import kengine.objects.glfw.Window
import kengine.util.Event
import org.lwjgl.glfw.GLFW.*

class KERuntime private constructor(): Event.Manager() {
    companion object {
        private val instance = KERuntime()

        var scene
            get() = instance.scene
            set(s) {
                if (instance.isInit) s.init()
                instance.scene = s
            }
        var window
            get() = instance.window
            set(w) { instance.window = w }

        fun run() = instance.run()

        fun doubleTime() = System.nanoTime() / 1_000_000_000.0
    }

    lateinit var window: Window
    lateinit var scene: Scene

    var vSync = true

    // Pass global events to Scripts
    @Event.Listener(eventClass = Event::class)
    fun onEvent(evt: Event) = scene.root.update(evt)

    private var isInit = false

    fun init() {
        if (isInit) return

        window.init()
        window.listeners.add(this)

        Shader.init()
        scene.init()

        glfwSwapInterval(if (vSync) 1 else 0)

        isInit = true
    }

    fun run() {
        init()

        val start = doubleTime()
        var t = start

        while (!glfwWindowShouldClose(window.id))
            t = scene.update(t, start)

        glfwTerminate()
    }
}