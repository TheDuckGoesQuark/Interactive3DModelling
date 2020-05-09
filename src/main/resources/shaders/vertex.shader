#version 130

in vec3 in_position;
in vec3 in_colour;

out vec3 exColour;

void main() {
    gl_Position = vec4(in_position, 1.0);
    exColour = in_colour;
}