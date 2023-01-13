package demo

import entity.Entity2D
import entity.components.Camera2D
import entity.components.render.GridAtlasTexture
import entity.components.render.Text
import entity.components.render.Texture
import objects.Font
import objects.Runtime
import objects.gl.Image
import objects.gl.Window
import org.joml.Vector4f

fun main() {
    val window = Window(800, 600, "KEngine")
    window.clearColor = Vector4f(0.3f, 0.1f, 0.5f, 1.0f)

    val runtime = Runtime(window)

    val font = Font("/fonts/GeomanistBook.ttf", 16)

    runtime.root.children(
        Entity2D("fps")
            .with(Text(font, "0 fps"))
            .with(FpsCounter(window)),
        Entity2D("player")
            .with(Texture(Image("/images/test.png")))
            .with(Camera2D(true))
            .with(PlayerController()),
        Entity2D("testAtlas")
            .with(GridAtlasTexture(Image("/images/test.png"), 2, 2))
            .with(CycleAtlas())
    )

    runtime.loop()
}