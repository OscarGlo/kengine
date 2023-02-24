import kengine.entity.components.Camera2D
import kengine.entity.components.Script
import kengine.entity.components.physics.Body2D
import kengine.math.Vector2f
import org.lwjgl.glfw.GLFW.*

class PlayerController : Script() {
    lateinit var camera: Camera2D
    lateinit var body: Body2D

    private var direction = Vector2f()
    private val speed = 50

    override fun onKey(key: Int, scancode: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS || action == GLFW_RELEASE) {
            // Character movement
            val sign = if (action == GLFW_PRESS) 1 else -1
            when (key) {
                GLFW_KEY_LEFT -> direction.x -= sign
                GLFW_KEY_RIGHT -> direction.x += sign
                GLFW_KEY_DOWN -> direction.y -= sign
                GLFW_KEY_UP -> direction.y += sign
            }

            // On press
            if (action == GLFW_PRESS) when (key) {
                GLFW_KEY_SPACE -> camera.current = !camera.current
            }
        }
    }

    override fun update(delta: Double, time: Double) {
        val dir = direction.normalize() * speed.toFloat()
        body.velocity.add(dir).multiply(0.9f)
    }
}