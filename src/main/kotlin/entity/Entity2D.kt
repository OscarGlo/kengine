package entity

import entity.components.Transform2D

open class Entity2D(id: String) : Entity(id) {
    init {
        with(Transform2D())
    }
}