#version 120

uniform int time; // Passed in, see ShaderHelper.java

uniform float alpha;
uniform vec3 lighting;
uniform sampler2D image;

void main() {
    vec2 texcoord = vec2(gl_TexCoord[0].st);
    vec4 color = texture2D(image, texcoord);

    gl_FragColor = gl_Color * vec4(color.r * lighting.r, color.g * lighting.g, color.b * lighting.b, color.a * alpha);
}