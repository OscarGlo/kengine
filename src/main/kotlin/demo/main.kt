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
import objects.Font
import objects.Runtime
import objects.gl.Image
import objects.gl.Window
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector4f

fun main() {
    val window = Window(Vector2i(800, 600), "KEngine")
    window.clearColor = Vector4f(0.3f, 0.1f, 0.5f, 1.0f)

    val runtime = Runtime(window)

    val font = Font("/fonts/GeomanistBook.ttf", 16)
    val image = Image("/images/circle.png")

    runtime.root.children(
        Entity2D(
            "fps",
            Text(font, "0 fps"),
            FpsCounter(window)
        ),
        Entity2D(
            "rect",
            Rect(Vector2f(50f, 100f), Vector4f(0.5f, 0.2f, 0.8f, 1.0f)),
            RectCollider(Vector2f(50f, 100f))
        ),
        Entity(
            "circle",
            Transform2D().translate(Vector2f(150f, 0f)),
            Ellipse(Vector2f(50f, 50f), 32, Vector4f(0.5f, 0.2f, 0.8f, 1.0f)),
            CircleCollider(25f)
        ),
        Entity2D(
            "player",
            Texture(image),
            CircleCollider(image.size.x / 2f),
            Camera2D(true),
            PlayerController()
        )
    )

    runtime.loop()
}