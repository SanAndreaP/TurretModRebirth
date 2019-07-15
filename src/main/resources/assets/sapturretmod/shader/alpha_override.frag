#version 120

uniform int time; // Passed in, see ShaderHelper.java

uniform float alpha;
uniform sampler2D image;
uniform sampler2D lighting;

void main() {
    vec2 texcoord = vec2(gl_TexCoord[0].st);
    vec4 color = texture2D(image, texcoord);
    vec2 texcoordL = vec2(gl_TexCoord[1].st);
    vec4 colorL = texture2D(lighting, texcoordL);

    gl_FragColor = gl_Color * vec4(colorL.r, colorL.g, colorL.b, 1.0) * vec4(color.r, color.g, color.b, color.a * alpha);
}