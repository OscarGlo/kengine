#version 460 core

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

struct Light {
    vec3 position;
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
};

in vec3 pos;
in vec3 normal;

uniform vec3 viewPos;
uniform Material material;
uniform Light light;

out vec4 FragColor;

void main() {
    vec3 lightDir = normalize(light.position - pos);
    vec3 viewDir = normalize(viewPos - pos);

    vec4 ambient = light.ambient * material.ambient;

    float diffuseStrength = max(dot(normal, lightDir), 0.0);
    vec4 diffuse = light.diffuse * material.diffuse * diffuseStrength;

    float specularStrength = pow(max(dot(viewDir, reflect(-lightDir, normal)), 0.0), material.shininess);
    vec4 specular = light.specular * material.specular * specularStrength;

    FragColor = vec4(vec3(ambient + diffuse + specular), 1);
}