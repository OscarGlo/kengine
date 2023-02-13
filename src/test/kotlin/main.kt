
import kengine.entity.Entity
import kengine.entity.components.Camera2D
import kengine.entity.components.Transform2D
import kengine.entity.components.physics.Body2D
import kengine.entity.components.physics.CircleCollider
import kengine.entity.components.physics.RectCollider
import kengine.entity.components.render.Ellipse
import kengine.entity.components.render.Rect
import kengine.entity.components.render.Text
import kengine.entity.components.render.Texture
import kengine.entity.components.render.tilemap.Tilemap
import kengine.objects.Font
import kengine.objects.Runtime
import kengine.objects.gl.Image
import kengine.objects.gl.Window
import kengine.objects.util.Resource
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f

fun main() {
    Resource.localPath = "src/test/resources"

    val window = Window(Vector2i(800, 600), "KEngine")
    window.clearColor = Vector4f(0.3f, 0.1f, 0.5f, 1.0f)

    val runtime = Runtime(window)

    // Resources
    val font = Font("/fonts/GeomanistBook.ttf", 16)
    val circle = Image("/images/circle.png")
    val tilemap = Image("/images/autotilemap_corner.png")

    val tiles = listOf(
        Vector2i(0, 1),
        Vector2i(0, 2),
        Vector2i(1, 0),
        Vector2i(1, 1),
        Vector2i(1, 2),
        Vector2i(2, 0),
        Vector2i(2, 1),
        Vector2i(3, 2)
    ).associateWith { Tilemap.Ref(0, true) }

    runtime.root.children(
        Entity(
            "background",
            Transform2D(),
            Tilemap(Vector2f(32f), Tilemap.cornerTileset(tilemap, 0), tiles.toMutableMap())
        ),
        Entity(
            "rect",
            Transform2D(),
            Rect(Vector2f(50f, 100f), Vector4f(0.5f, 0.2f, 0.8f, 0.5f)),
            RectCollider(Vector2f(50f, 100f)),
            Body2D(true)
        ),
        Entity(
            "circle",
            Transform2D().translate(Vector2f(150f, 0f)),
            Ellipse(Vector2f(50f, 50f), 32, Vector4f(0.5f, 0.2f, 0.8f, 0.5f)),
            CircleCollider(25f),
            Body2D(true)
        ),
        Entity(
            "player",
            Transform2D().translate(Vector2f(100f, 100f)),
            Texture(circle),
            CircleCollider(circle.size.x / 2f),
            Camera2D(true),
            Body2D(),
            PlayerController()
        ),
        Entity(
            "fps",
            Transform2D(true),
            Text(font, "0 fps"),
            FpsCounter(window)
        )
    )

    runtime.loop()
}