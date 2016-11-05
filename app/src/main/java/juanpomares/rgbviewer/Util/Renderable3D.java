package juanpomares.rgbviewer.Util;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import juanpomares.rgbviewer.R;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;


public class Renderable3D
{
    protected int tipo;

    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData;

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 4;
    // Cálculo del tamaño de los datos (5 floats)
    private int STRIDE;

    private int mVertexCount;
    private float[] mVertexArray;
    private LinkedList<Point3D> mVertexList;

    private Color color;



    /*Variables para cuadrica*/
    private int aPositionLocation;
    public int aColorLocation;
    public int uMVPMatrixLocation;

    private static final String U_MVPMATRIX = "u_MVPMatrix";
    private static final String A_POSITION  = "a_Position";
    private static final String A_COLOR     = "a_Color";

    private int program;

    public Renderable3D(int _type, LinkedList<Point3D> _list)
    {
        STRIDE=POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT;

        tipo=_type;
        STRIDE*=BYTES_PER_FLOAT;
        createLists(_list);
    }

    public void createLists(LinkedList<Point3D> _list)
    {
        mVertexList =_list;
        if(mVertexArray !=null)
            mVertexArray =null;

        mVertexCount =mVertexList.size();
        mVertexArray = new float[(mVertexCount =this.mVertexList.size())*(POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT)];

        int desplazamiento=0;

        Point3D actual;
        Color color;

        for (int i = 0; i< mVertexCount; i++)
        {
            actual = mVertexList.get(i);

            mVertexArray[desplazamiento++] = actual.getX();
            mVertexArray[desplazamiento++] = actual.getY();
            mVertexArray[desplazamiento++] = actual.getZ();

            color =actual.getColor();
            mVertexArray[desplazamiento++] = color.getValueR();
            mVertexArray[desplazamiento++] = color.getValueG();
            mVertexArray[desplazamiento++] = color.getValueB();
            mVertexArray[desplazamiento++] = color.getValueA();
        }

        if(vertexData!=null)
        {
            vertexData.clear();
            vertexData = null;
        }

        vertexData = ByteBuffer
                .allocateDirect(mVertexArray.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.clear();
        vertexData.put(mVertexArray);
    }


    public void onSurfaceCreated(Context context)
    {
        String vertexShaderSource="", fragmentShaderSource="";

        vertexShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        fragmentShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        // Compilamos los shaders
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        // En depuración validamos el programa OpenGL
        /*if (LoggerConfig.ON) {*/	ShaderHelper.validateProgram(program);	//}

        // Activamos el programa OpenGL
        glUseProgram(program);

        uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation =    glGetAttribLocation(program, A_COLOR);


        glEnableVertexAttribArray(aPositionLocation);
        glEnableVertexAttribArray(aColorLocation);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void loadBufferShader()
    {
        // Asociando vértices con su attribute
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);


        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
    }


    public void draw(float[] MVP, float[] modelMatrix)
    {
        if (vertexData == null)
            return;

        glUseProgram(program);
        loadBufferShader();


        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVP, 0);


        glDrawArrays(tipo, 0, mVertexCount);
    }

}