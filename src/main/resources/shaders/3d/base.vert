#version 460 core

layout (location = 0) in vec3 _pos;
layout (location = 1) in vec3 _normal;
layout (location = 2) in vec2 _uv;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec3 pos;
out vec3 normal;
out vec2 uv;

void main() {
    gl_Position = projection * view * model * vec4(_pos, 1);

    pos = vec3(model * vec4(_pos, 1));
    normal = normalize(mat3(transpose(inverse(model))) * _normal);
    uv = _uv;
}