package kengine.entity.components.physics

import kengine.math.Vector2f

class RectCollider(size: Vector2f, offset: Vector2f = Vector2f()) : Collider2D(
    listOf(
        Vector2f(size.x / 2, size.y / 2),
        Vector2f(size.x / 2, -size.y / 2),
        Vector2f(-size.x / 2, -size.y / 2),
        Vector2f(-size.x / 2, size.y / 2)
    ),
    offset
)