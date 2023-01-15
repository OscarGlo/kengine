package entity

import entity.components.Transform2D

open class Entity2D(id: String, vararg components: Component) : Entity(id, Transform2D(), *components)