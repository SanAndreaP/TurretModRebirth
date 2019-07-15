#version 120

uniform int time; // Passed in, see ShaderHelper.java

uniform float alpha;
uniform float brightness;
uniform sampler2D image;

void main() {
    vec2 texcoord = vec2(gl_TexCoord[0].st);
    vec4 color = texture2D(image, texcoord);

    gl_FragColor = gl_Color * vec4(color.r * brightness, color.g * brightness, color.b * brightness, color.a * alpha);
}