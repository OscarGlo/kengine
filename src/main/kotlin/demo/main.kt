package demo

import entity.Entity
import entity.Entity2D
import entity.components.Camera2D
import entity.components.Transform2D
import entity.components.physics.CircleCollider
import entity.components.physics.RectCollider
import entity.components.render.Ellipse
import entity.components.render.Rect
import entity.components.render.Text
import entity.components.render.Texture
import entity.components.render.tilemap.Tilemap
import objects.Font
import objects.Runtime
import objects.gl.Image
import objects.gl.Window
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f
import kotlin.random.Random

fun main() {
    val window = Window(Vector2i(800, 600), "KEngine")
    window.clearColor = Vector4f(0.3f, 0.1f, 0.5f, 1.0f)

    val runtime = Runtime(window)

    val font = Font("/fonts/GeomanistBook.ttf", 16)
    val circle = Image("/images/circle.png")
    val img = Image("/images/autotilemap.png")

    val tiles = (0..50).associate {
        Vector2i(Random.nextInt(10), Random.nextInt(10)) to Tilemap.Ref(0, true)
    }

    runtime.root.children(
        Entity2D(
            "background",
            Tilemap(Vector2f(32f), Tilemap.edgeTileset(img, 0), tiles.toMutableMap())
        ),
        Entity2D(
            "rect",
            Rect(Vector2f(50f, 100f), Vector4f(0.5f, 0.2f, 0.8f, 0.5f)),
            RectCollider(Vector2f(50f, 100f))
        ),
        Entity(
            "circle",
            Transform2D().translate(Vector2f(150f, 0f)),
            Ellipse(Vector2f(50f, 50f), 32, Vector4f(0.5f, 0.2f, 0.8f, 0.5f)),
            CircleCollider(25f)
        ),
        Entity2D(
            "player",
            Texture(circle),
            CircleCollider(circle.size.x / 2f),
            Camera2D(true),
            PlayerController()
        ),
        Entity2D(
            "fps",
            Text(font, "0 fps"),
            FpsCounter(window)
        )
    )

    runtime.loop()
}