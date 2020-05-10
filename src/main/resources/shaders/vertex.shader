#version 130

in vec3 in_position;
in vec3 in_colour;

out vec3 exColour;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * vec4(in_position, 1.0);
    exColour = in_colour;
}