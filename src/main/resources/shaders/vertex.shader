#version 130

in vec3 in_position;
in vec3 in_colour;
in vec3 in_normal;

out vec3 exColour;

uniform mat4 worldMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * viewMatrix * worldMatrix * vec4(in_position, 1.0);
    exColour = in_colour;
}

