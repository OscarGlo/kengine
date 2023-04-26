package kengine.entity.components.render.gui

import kengine.math.Color
import kengine.objects.Font
import kengine.objects.glfw.Cursor
import kengine.util.Resource

class Theme {
    companion object {
        var default: Theme = Theme()
    }

    var font = Font(Resource("/fonts/JetBrainsMono.ttf", false), 14)

    var defaultCursor = Cursor.arrow
    var resizeHCursor = Cursor.resizeH
    var resizeVCursor = Cursor.resizeV
    var resizeNWSECursor = Cursor.resizeNWSE
    var resizeNESWCursor = Cursor.resizeNESW

    var textColor = Color(0.95f)
    var backgroundColor = Color(0.2f, 0.2f, 0.25f)
    var accentColor = Color(0.1f, 0.1f, 0.15f)
    var activeColor = Color(0.15f, 0.15f, 0.2f)

    var topbarHeight = 20f
    var borderWidth = 5f
}