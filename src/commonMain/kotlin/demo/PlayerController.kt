package demo

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.entity.components.physics.Body2D
import kengine.entity.components.render.Camera
import kengine.math.Quaternion
import kengine.math.Vector2f
import kengine.math.Vector3f
import kengine.objects.glfw.KeyEvent
import kengine.util.Key

class PlayerController : Entity.Component() {
    lateinit var tf: Transform
    lateinit var camera: Camera
    lateinit var body: Body2D

    private var direction = Vector2f()
    private var rotation = 0f
    private var scale = 0f
    private val speed = 50

    override suspend fun init() {
        tf = entity.get<Transform>()
        camera = entity.get<Camera>()
        body = entity.get<Body2D>()

        listener(this::onKey)
    }

    fun onKey(evt: KeyEvent) {
        if (!evt.repeat) {
            // Character movement
            val sign = if (evt.pressed) 1 else -1
            when (evt.key) {
                Key.LEFT -> direction.x -= sign
                Key.RIGHT -> direction.x += sign
                Key.DOWN -> direction.y -= sign
                Key.UP -> direction.y += sign

                Key.R -> rotation += sign

                Key.EQUAL -> scale += sign
                Key.MINUS -> scale -= sign

                else -> {}
            }

            // On press
            if (evt.pressed) when (evt.key) {
                Key.SPACE -> camera.current = !camera.current
                else -> {}
            }
        }
    }

    override fun update(delta: Double) {
        val dir = direction.normalize() * speed.toFloat()
        body.velocity.add(dir).multiply(0.9f)

        tf.rotate(Quaternion.axisAngle(Vector3f.front, 5 * delta.toFloat() * rotation))
        tf.scale(Vector3f(1f) + scale * delta.toFloat())
    }
}