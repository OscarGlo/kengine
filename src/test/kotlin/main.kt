import kengine.entity.Entity
import kengine.entity.components.*
import kengine.entity.components.physics.Body2D
import kengine.entity.components.physics.CircleCollider
import kengine.entity.components.render.Ellipse
import kengine.entity.components.render.ParticleSpawner
import kengine.entity.components.render.Tilemap
import kengine.entity.components.render.gui.*
import kengine.entity.components.render.gui.input.Button
import kengine.entity.components.render.image.Texture
import kengine.math.*
import kengine.objects.Font
import kengine.objects.Runtime
import kengine.objects.gl.Image
import kengine.objects.gl.Window
import kengine.util.Event
import kengine.util.Resource
import java.util.*

fun main() {
    Resource.localPath = "src/test/resources"

    val window = Window(Vector2i(800, 600), "KEngine")
    window.clearColor = Color(0.3f, 0.1f, 0.5f)

    val runtime = Runtime(window)

    // Resources
    Resource.addLanguage(Locale.ENGLISH, "lang/en.txt")
    Resource.addLanguage(Locale.FRENCH, "lang/fr.txt")

    val font = Font("/fonts/GeomanistBook.ttf", 16)
    val circle = Image("/images/circle.png", filter = false)
    val tilemap = Image("/images/tilemaps/full.png", filter = false)

    val tiles = (0..100).map {
        Vector2i(Vector2f.random() * 10f)
    }.associateWith { Tilemap.Ref(0, true) }

    runtime.root.children(
        Entity(
            "background",
            Transform2D(),
            Tilemap(Vector2f(32f), Tilemap.fullTileset(tilemap, 0), tiles.toMutableMap())
        ),
        Entity(
            "circle",
            Transform2D().apply { matrix.translate(Vector3f(25f, 0f, 0f)) },
            Ellipse(Vector2f(50f, 50f), 32, Color(0.5f, 0.2f, 0.8f, 0.5f)),
            CircleCollider(25f),
            Body2D(true),
            Animator(
                Animator.Animation(
                    { get<Transform2D>().matrix }, 1.0, true, true,
                    Animator.PropertyAnimation(
                        Matrix4::position,
                        Animator.Keyframe(Vector3f(0f), 0.0, Shaping.smoothstep),
                        Animator.Keyframe(Vector3f(100f, 100f, 0f), 0.5, Shaping.smoothstep)
                    )
                )
            )
        ),
        Entity(
            "player",
            Transform2D().apply { matrix.translate(Vector3f(100f, 100f, 0f)) },
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
            Camera2D(true),
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
        ).children(
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

    runtime.run()
}