package demo

import kengine.entity.Entity
import kengine.entity.components.Transform
import kengine.entity.components.render.Camera
import kengine.entity.components.render.gui.Text
import kengine.entity.components.render.gui.UINode
import kengine.entity.components.render.r3d.Render3D
import kengine.math.*
import kengine.objects.KERuntime
import kengine.objects.Scene
import kengine.objects.gl.GLImage
import kengine.objects.glfw.CursorMode
import kengine.objects.glfw.KeyEvent
import kengine.objects.glfw.MouseMoveEvent
import kengine.objects.glfw.Window
import kengine.util.Key
import kengine.util.Resource
import kengine.util.rectIndicesN
import kotlin.jvm.JvmStatic
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

class Shape(id: String, pos: Vector3f, texture: GLImage) : Entity(
    id,
    Transform(true).translate(pos).scale(Vector3f(2f)),
    object : Render3D(
        floatArrayOf(
            -1f, -1f, 1f, 0f, 0f, 1f, 0f, 1f,
            -1f, 1f, 1f, 0f, 0f, 1f, 0f, 0f,
            1f, 1f, 1f, 0f, 0f, 1f, 1f, 0f,
            1f, -1f, 1f, 0f, 0f, 1f, 1f, 1f,

            -1f, 1f, -1f, 0f, 1f, 0f, 0f, 1f,
            -1f, 1f, 1f, 0f, 1f, 0f, 0f, 0f,
            1f, 1f, 1f, 0f, 1f, 0f, 1f, 0f,
            1f, 1f, -1f, 0f, 1f, 0f, 1f, 1f,

            1f, -1f, -1f, 1f, 0f, 0f, 0f, 1f,
            1f, -1f, 1f, 1f, 0f, 0f, 0f, 0f,
            1f, 1f, 1f, 1f, 0f, 0f, 1f, 0f,
            1f, 1f, -1f, 1f, 0f, 0f, 1f, 1f,

            -1f, -1f, -1f, 0f, 0f, -1f, 0f, 1f,
            -1f, 1f, -1f, 0f, 0f, -1f, 0f, 0f,
            1f, 1f, -1f, 0f, 0f, -1f, 1f, 0f,
            1f, -1f, -1f, 0f, 0f, -1f, 1f, 1f,

            -1f, -1f, -1f, 0f, -1f, 0f, 0f, 1f,
            -1f, -1f, 1f, 0f, -1f, 0f, 0f, 0f,
            1f, -1f, 1f, 0f, -1f, 0f, 1f, 0f,
            1f, -1f, -1f, 0f, -1f, 0f, 1f, 1f,

            -1f, -1f, -1f, -1f, 0f, 0f, 0f, 1f,
            -1f, -1f, 1f, -1f, 0f, 0f, 0f, 0f,
            -1f, 1f, 1f, -1f, 0f, 0f, 1f, 0f,
            -1f, 1f, -1f, -1f, 0f, 0f, 1f, 1f,
        ),
        rectIndicesN(6),
        texture
    ) {
        override fun renderSteps() {
            bindShader(phong)
            images[0].bind()
            triangles(12)
        }
    },
    object : Component() {
        lateinit var tf: Transform
        override fun update(delta: Double) {
            tf.rotation = Quaternion.euler(entity.time.toFloat())
        }
    }
)

object Main3D {
    @JvmStatic
    suspend fun main() {
        //Resource.localPath = "src/jvmTest/resources"

        KERuntime.window = Window(Vector2i(800, 600), "KEngine").apply {
            cursorMode = CursorMode.Locked
        }

        val square = GLImage(Resource("images/square.png"), filter = false)

        KERuntime.set(Scene(
            Entity(
                "camera",
                Transform(true).translate(Vector3f(0f, 0f, 8f)),
                Camera(true),
                object : Entity.Component() {
                    lateinit var tf: Transform
                    lateinit var cam: Camera

                    val direction = Vector3f()
                    var angle = Vector2f()

                    private lateinit var mousePos: Vector2f

                    override suspend fun init() {
                        tf = entity.get<Transform>()
                        cam = entity.get<Camera>()

                        listener(this::keyPress)
                        listener(this::mouseMove)

                        mousePos = KERuntime.window.mousePosition
                    }

                    fun keyPress(evt: KeyEvent) {
                        val n = if (evt.repeat) 0f else if (evt.pressed) 5f else -5f
                        when (evt.key) {
                            Key.A -> direction.x -= n
                            Key.D -> direction.x += n
                            Key.LEFT_SHIFT -> direction.y -= n
                            Key.SPACE -> direction.y += n
                            Key.W -> direction.z -= n
                            Key.S -> direction.z += n
                            else -> {}
                        }
                    }

                    fun mouseMove(evt: MouseMoveEvent) {
                        val delta = (evt.position - mousePos) / 100f
                        angle += delta
                        angle.y = min(max(angle.y, -PI.toFloat() / 2), PI.toFloat() / 2)
                        mousePos = evt.position
                    }

                    override fun update(delta: Double) {
                        tf.rotation = Quaternion.euler(angle.y, -angle.x, 0f)
                        tf.translate((cam.right * direction.x + cam.up * direction.y + cam.front * direction.z) * delta.toFloat())
                    }
                }
            ),
            Shape("test1", Vector3f(0f, 0f, 0f), square),
            Shape("test2", Vector3f(0f, -5f, 0f), square),
            Shape("test3", Vector3f(0f, 5f, 0f), square),
            Shape("test4", Vector3f(-5f, 0f, 0f), square),
            Shape("test5", Vector3f(5f, 0f, 0f), square),

            Entity(
                "fps",
                Text().with(UINode.Position(top = 5f, left = 5f)),
                FpsCounter()
            ),
            Entity("init", object : Entity.Component() {
                override suspend fun init() = Render3D.phong.let {
                    it.use()
                    it["viewPos"] = KERuntime.scene.currentCamera?.entity?.get<Transform>()?.position ?: Vector3f()

                    it["material.ambient"] = Color(0.2f, 0.4f, 0.3f)
                    it["material.diffuse"] = Color(0.2f, 0.9f, 0.3f)
                    it["material.specular"] = Color(1f)
                    it["material.shininess"] = 8f

                    it["light.position"] = Vector3f(5f, 5f, 10f)
                    it["light.ambient"] = Color(0.2f)
                    it["light.diffuse"] = Color(0.8f)
                    it["light.specular"] = Color(0.5f)
                }
            })
        )
        )

        KERuntime.run()
    }
}