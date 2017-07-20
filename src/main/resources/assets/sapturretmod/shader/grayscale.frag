#version 120

uniform int time; // Passed in, see ShaderHelper.java

uniform sampler2D image;

void main() {
    vec2 texcoord = vec2(gl_Tex[0]);
    vec4 color = texture2D(image, texcoord);

    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    color.rgb = vec3(gray) * 0.75;

    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
}