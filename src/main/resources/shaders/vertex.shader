#version 130

in vec3 in_position;
in vec3 in_colour;
in vec3 in_normal;

out vec3 exColour;
out vec3 mvVertexNormal;
out vec3 mvVertexPos;

uniform mat4 worldMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main() {
    mat4 modelViewMatrix = viewMatrix * worldMatrix;
    vec4 mvPos =  modelViewMatrix * vec4(in_position, 1.0);
    gl_Position = projectionMatrix * mvPos;
    exColour = in_colour;
    mvVertexNormal = normalize(modelViewMatrix * vec4(mvVertexNormal, 0.0)).xyz;
    mvVertexPos = mvPos.xyz;
}

