// blur.frag
#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float blur;

void main() {
    vec4 sum = vec4(0.0);
    float blurSize = blur / 300.0;

    sum += texture2D(u_texture, v_texCoords + vec2(-4.0*blurSize, -4.0*blurSize)) * 0.05;
    sum += texture2D(u_texture, v_texCoords + vec2(-3.0*blurSize, -3.0*blurSize)) * 0.09;
    sum += texture2D(u_texture, v_texCoords + vec2(-2.0*blurSize, -2.0*blurSize)) * 0.12;
    sum += texture2D(u_texture, v_texCoords + vec2(-1.0*blurSize, -1.0*blurSize)) * 0.15;
    sum += texture2D(u_texture, v_texCoords) * 0.16;
    sum += texture2D(u_texture, v_texCoords + vec2(1.0*blurSize, 1.0*blurSize)) * 0.15;
    sum += texture2D(u_texture, v_texCoords + vec2(2.0*blurSize, 2.0*blurSize)) * 0.12;
    sum += texture2D(u_texture, v_texCoords + vec2(3.0*blurSize, 3.0*blurSize)) * 0.09;
    sum += texture2D(u_texture, v_texCoords + vec2(4.0*blurSize, 4.0*blurSize)) * 0.05;

    gl_FragColor = sum * v_color;
}
