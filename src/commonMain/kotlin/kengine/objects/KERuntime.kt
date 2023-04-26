package kengine.objects

import kengine.objects.gl.Shader
import kengine.objects.glfw.Window
import kengine.util.Event
import kengine.util.doubleTime
import kengine.util.terminate

class KERuntime private constructor(): Event.Manager() {
    companion object {
        private val instance = KERuntime()

        var window
            get() = instance.window
            set(w) { instance.window = w }

        val scene get() = instance.scene

        suspend fun set(scene: Scene) {
            if (instance.isInit) scene.init()
            instance.scene = scene
        }

        val root get() = scene.root

        suspend fun run() = instance.run()

        var time: Double = 0.0
    }

    lateinit var window: Window
    lateinit var scene: Scene

    var vSync = true

    private var isInit = false

    suspend fun init() {
        if (isInit) return

        window.init()
        window.vSync = vSync

        Shader.init()
        scene.init()

        isInit = true
    }

    suspend fun run() {
        init()

        var t = doubleTime()
        while (!window.closed)
            t = scene.update(t)

        terminate()
    }
}