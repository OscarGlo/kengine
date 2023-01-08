#version 460 core

layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTexCoord;

uniform mat4 transform;

out vec2 texCoord;

void main() {
    gl_Position = transform * vec4(aPos, 0, 1);
    texCoord = aTexCoord;
}