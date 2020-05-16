#version 130

in vec3 exColour;
out vec4 fragColor;

struct DirectionalLight {
    vec3 colour;
    vec3 direction;
    float intensity;
};

uniform DirectionalLight directionalLight;

void main() {
    fragColor = vec4(exColour, 1.0);
}