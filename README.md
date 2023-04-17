# kengine

A _WIP_ game engine written in [Kotlin](https://kotlinlang.org/) using [LWJGL](https://www.lwjgl.org/) bindings.
The aim is to create a "code-centric" game engine with full capabilities, while trying to make the developer experience
as good as possible. The engine architecture is an entity-component tree similar to Unity's.


## Features

### General
- Event system
- Fully extensible entities and components

### Math
- Custom math library using genericity for simpler usage
- Convertible `Vector` types `Vector2f`, `Vector2i`, `Vector3f`, ...
- `Matrix4` to represent transforms

### Rendering
- Switchable `Camera`
- Basic colored `Rectangle` and `Ellipse`
- `Texture` as well as `AtlasTexture` and `GridAtlasTexture`
- `Tilemap` with tilesets and auto-tiling
- Configurable `ParticleSpawner`

### GUI Components
- `Theme` to configure UI elements and their children's display
- Draggabble `UIWindow`
- `Text` using generated bitmap fonts
- `UIImage`
- `Button`

### Other
- `Script` written in Kotlin using the engine classes
- `Animator` to animate and interpolate any property on any object
- `Body2D` and `Collider2D` for simple 2D physics
- `AudioPlayer` for .ogg files


## //TODO

- **Bugfixes**


- `Matrix4`
  - Constructor from Transform properties (position, scale, rotation)


- `Tilemap`
  - Tile collisions
  - World/tile position conversion


- `UIWindow`
  - User resize / resizable property
  - Close button / closable property
  - Min / Max size


- `AudioPlayer`
  - Pausing on entity pause (pause event?)


- **Optimization**
  - Collisions
  - Fix input handling lag


- **New features**


- **Future ideas**
  - Modding via mixin injection