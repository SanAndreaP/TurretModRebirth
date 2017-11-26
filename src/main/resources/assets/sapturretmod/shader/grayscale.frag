#version 120

uniform int time; // Passed in, see ShaderHelper.java

uniform sampler2D image;
uniform float brightness;

void main() {
    vec2 texcoord = gl_TexCoord[0].st;
    vec4 color = texture2D(image, texcoord);

    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    color.rgb = vec3(gray) * brightness;

    gl_FragColor = gl_Color * vec4(color.r, color.g, color.b, color.a);
}