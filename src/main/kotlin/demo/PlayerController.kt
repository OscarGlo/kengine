package demo

import entity.components.Camera2D
import entity.components.Script
import entity.components.Transform2D
import entity.components.physics.Collider2D
import entity.components.render.Texture
import org.joml.Vector2f
import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.*
import util.white

class PlayerController : Script() {
    lateinit var camera: Camera2D
    lateinit var texture: Texture
    lateinit var collider: Collider2D
    lateinit var transform: Transform2D

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
        // Collision
        texture.color = if (collider.collisions.isNotEmpty())
            Vector4f(1f, 0.5f, 0.5f, 1f)
        else white

        // Movement
        val dir = direction
            .run { if (direction.length() > 0) normalize(Vector2f()) else this }
            .mul(speed * delta / 1000f, Vector2f())
        velocity.add(dir).mul(0.9f)
        transform.translate(velocity)
    }
}