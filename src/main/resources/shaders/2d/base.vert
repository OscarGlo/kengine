#version 460 core

layout (location = 0) in vec2 pos;
layout (location = 1) in vec2 _uv;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec2 uv;

void main() {
    gl_Position = projection * view * model * vec4(pos, 0, 1);
    uv = _uv;
}