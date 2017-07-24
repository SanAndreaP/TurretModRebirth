#version 120

uniform int time; // Passed in, see ShaderHelper.java

uniform float alpha;
uniform sampler2D image;

void main() {
    vec2 texcoord = vec2(gl_TexCoord[0]);
    vec4 color = texture2D(image, texcoord);

    gl_FragColor = vec4(color.r, color.g, color.b, color.a * alpha);
}