import kengine.entity.Entity
import kengine.entity.components.*
import kengine.entity.components.anim.*
import kengine.entity.components.physics.Body2D
import kengine.entity.components.physics.CircleCollider
import kengine.entity.components.render.Camera
import kengine.entity.components.render.gui.*
import kengine.entity.components.render.gui.input.Button
import kengine.entity.components.render.r2d.Ellipse
import kengine.entity.components.render.r2d.ParticleSpawner
import kengine.entity.components.render.r2d.Tilemap
import kengine.entity.components.render.r2d.image.Texture
import kengine.math.*
import kengine.objects.Font
import kengine.objects.KERuntime
import kengine.objects.Scene
import kengine.objects.gl.GLImage
import kengine.objects.glfw.GLFWImageWrapper
import kengine.objects.glfw.Window
import kengine.util.Event
import kengine.util.Resource
import java.util.*

fun main() {
    Resource.localPath = "src/jvmTest/resources"

    KERuntime.window = Window(Vector2i(800, 600), "KEngine").apply {
        clearColor = Color(0.3f, 0.1f, 0.5f)
        icon = GLFWImageWrapper("images/square.png")
    }

    // Resources
    Resource.addLanguage(Locale.ENGLISH, "lang/en.txt")
    Resource.addLanguage(Locale.FRENCH, "lang/fr.txt")

    val font = Font("/fonts/GeomanistBook.ttf", 16)
    val circle = GLImage("/images/circle.png", filter = false)
    val tilemap = GLImage("/images/tilemaps/full.png", filter = false)

    val tiles = (0..100).map {
        Vector2i(Vector2f.random() * 10f)
    }.associateWith { Tilemap.Ref(0, true) }

    KERuntime.scene = Scene(
        Entity(
            "background",
            Transform(),
            Tilemap(Vector2f(32f), Tilemap.fullTileset(tilemap, 0), tiles.toMutableMap())
        ),
        Entity(
            "circle",
            Transform().translate(Vector3f(25f, 0f, 0f)),
            Ellipse(Vector2f(50f, 50f), 32, Color(0.5f, 0.2f, 0.8f, 0.5f)),
            CircleCollider(25f),
            Body2D(true),
            Animator(
                Animation(
                    { get<Transform>() }, 1.0, true, true,
                    PropertyAnimation(
                        Transform::position,
                        Keyframe(Vector3f(0f), 0.0, Shaping.smoothstep),
                        Keyframe(Vector3f(100f, 100f, 0f), 0.5, Shaping.smoothstep)
                    )
                )
            )
        ),
        Entity(
            "player",
            Transform().translate(Vector3f(100f, 100f, 0f)),
            ParticleSpawner(
                0.05,
                0.5,
                4f,
                Color.white,
                Vector2f(0f),
                Vector2f(0f, -200f),
                Rect(-10f, -10f, 10f, 10f),
                endColor = Color(0f, 0f),
                sizeDelta = 1.5f,
                initialVelocityDelta = Vector2f(100f)
            ),
            Texture(circle),
            CircleCollider(circle.size.x / 2f),
            Camera(true),
            Body2D(),
            PlayerController()
        ),
        Entity(
            "fps",
            Text(Resource.getString("fps").format(0f))
                .with(Theme().also { it.font = font })
                .with(UINode.Position(top = 5f, left = 5f)),
            FpsCounter()
        ),
        Entity(
            "window",
            UIWindow(Vector2f(100f, 200f), "Window").with(UINode.Position(left = 20f))
        ).add(
            Entity(
                "button",
                Button(Vector2f(80f, 20f), "Button")
                    .with(UINode.Position(top = 25f)),
                object : Script() {
                    lateinit var button: Button

                    @Event.Listener(Button.PressedEvent::class)
                    fun onButtonPressed(evt: Button.PressedEvent) {
                        if (evt.button == button)
                            println("hi :)")
                    }
                }
            )
        )
    )

    KERuntime.run()
}