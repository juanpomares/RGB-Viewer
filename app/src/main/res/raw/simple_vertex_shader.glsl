uniform mat4 u_MVPMatrix;   		// in: Matriz Projection*ModelView


attribute vec4 a_Position;			// in: Posición de cada vértice
attribute vec4 a_Color;			// in: Normal de cada vértice

varying vec4 v_Color;				// out: Color de salida al fragment shader


void main()
{
gl_PointSize=20;
	gl_Position = u_MVPMatrix * a_Position;
	v_Color = a_Color;
}