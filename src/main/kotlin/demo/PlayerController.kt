package demo

import entity.components.Camera2D
import entity.components.Script
import entity.components.Transform2D
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*

class PlayerController : Script() {
    lateinit var camera: Camera2D

    private var direction = Vector2f()
    private var velocity = Vector2f()
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

            // Toggle camera
            if (action == GLFW_PRESS && key == GLFW_KEY_SPACE)
                camera.current = !camera.current
        }
    }

    override fun update(delta: Long, time: Long) {
        val dir = direction
            .run { if (direction.length() > 0) normalize(Vector2f()) else this }
            .mul(speed * delta / 1000f, Vector2f())
        velocity.add(dir).mul(0.9f)
        entity.get<Transform2D>().translate(velocity.x, velocity.y)
    }
}