package juanpomares.rgbviewer.Util;

import static android.opengl.GLES20.*;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import java.util.ArrayList;
import java.util.Iterator;

import juanpomares.rgbviewer.R;


public class OpenGLRenderer implements Renderer
{
    private static final float TAM = 0.5f;

    public Context context;
    private float rX=45, rY=-45;

    private float[] projectionMatrix = new float[16];
    private float[] modelMatrix=new float[16];
    private float[] MVP=new float[16];

    private boolean SurfaceCreated=false;

    private ArrayList<Renderable3D> renderables;

    private int aPositionLocation;
    private int aColorLocation;
    private int uSizeLocation;
    private int uMVPMatrixLocation;

    private static final String U_MVPMATRIX = "u_MVPMatrix";
    private static final String A_POSITION  = "a_Position";
    private static final String U_SIZE      = "u_Size";
    private static final String A_COLOR     = "a_Color";

    private int program;


    public OpenGLRenderer(Context context)
    {
        this.context = context;
        renderables=new ArrayList<Renderable3D>();
    }

    float Min(float a, float b){return a<b?a:b;}
    float Max(float a, float b){return a>b?a:b;}

    public void setRX(float NewRX)  { rX=Min(85f, Max(NewRX, 5f));  }
    public void setRY(float NewRY)  { rY=Min(-5f, Max(NewRY, -85f));}

    public float getRX(){return rX;}
    public float getRY(){return rY;}

    public void AddRenderable(Renderable3D _renderable)
    {
        renderables.add(_renderable);
        if(SurfaceCreated)
            _renderable.onSurfaceCreated(aPositionLocation, aColorLocation, uSizeLocation, uMVPMatrixLocation);
    }

    public void RemoveRenderable(Renderable3D _renderable)
    {
        renderables.remove(_renderable);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        String vertexShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource=TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        // Compilamos los shaders
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        // En depuración validamos el programa OpenGL
        /*if (LoggerConfig.ON) {*/	ShaderHelper.validateProgram(program);	//}

        glUseProgram(program);

        uMVPMatrixLocation  = glGetUniformLocation(program, U_MVPMATRIX);
        uSizeLocation       = glGetUniformLocation(program, U_SIZE);
        aPositionLocation   = glGetAttribLocation(program, A_POSITION);
        aColorLocation      = glGetAttribLocation(program, A_COLOR);



        glEnableVertexAttribArray(aPositionLocation);
        glEnableVertexAttribArray(aColorLocation);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glLineWidth(5);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        handleTouchDrag(0, 0);
        SurfaceCreated=true;


        Iterator<Renderable3D> rend_it= renderables.iterator();
        while(rend_it.hasNext())
            rend_it.next().onSurfaceCreated(aPositionLocation, aColorLocation, uSizeLocation, uMVPMatrixLocation);

        /* for(int i=0, length=renderables.size(); i<length; i++)
            renderables.get(i).onSurfaceCreated(aPositionLocation, aColorLocation, uSizeLocation, uMVPMatrixLocation);*/
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        // Establecer el viewport de  OpenGL para ocupar toda la superficie.
        glViewport(0, 0, width, height);
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) orthoM(projectionMatrix, 0, -aspectRatio*TAM, aspectRatio*TAM, -TAM, TAM, -10.0f, 10.0f);
        else orthoM(projectionMatrix, 0, -TAM, TAM, -aspectRatio*TAM, aspectRatio*TAM, -10.0f, 10.0f);

        multiplyMM(MVP, 0, projectionMatrix, 0, modelMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

        Iterator<Renderable3D> rend_it=renderables.iterator();
        while(rend_it.hasNext())
            rend_it.next().draw(MVP, modelMatrix);

         /*for(int i=0, length=renderables.size(); i<length; i++)
            renderables.get(i).draw(MVP, modelMatrix);*/
    }

    public void handleTouchDrag(float normalizedX, float normalizedY)
    {
        setRX(rX-normalizedY*45f);
        setRY(rY+normalizedX*45f);

        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, rX, 1f, 0f, 0f);
        rotateM(modelMatrix, 0, rY, 0f, 1f, 0f);
        scaleM(modelMatrix, 0, 0.65f, 0.65f, 0.65f);
        translateM(modelMatrix, 0, -0.5f, -0.5f, -0.5f);

        multiplyMM(MVP, 0, projectionMatrix, 0, modelMatrix, 0);
    }
}