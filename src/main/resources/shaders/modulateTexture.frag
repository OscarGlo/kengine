#version 460 core

in vec2 uv;

uniform sampler2D tex;
uniform vec4 color;

out vec4 FragColor;

void main() {
    FragColor = texture(tex, uv) * color;
}