uniform mat4 u_MVPMatrix;
uniform float u_Size;

attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;


void main()
{
    gl_PointSize=u_Size;
	gl_Position = u_MVPMatrix * a_Position;
	v_Color = a_Color;
}