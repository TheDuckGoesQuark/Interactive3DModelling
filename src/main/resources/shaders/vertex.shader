#version 130

in vec3 in_position;
in vec3 in_colour;

out vec3 exColour;

//struct DirectionalLight {
//    vec3 colour;
//    vec3 direction;
//    float intensity;
//};

//uniform DirectionalLight directionalLight;
uniform mat4 worldMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * worldMatrix * vec4(in_position, 1.0);
    exColour = in_colour;
}

