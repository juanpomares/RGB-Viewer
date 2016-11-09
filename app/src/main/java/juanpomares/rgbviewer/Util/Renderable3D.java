package juanpomares.rgbviewer.Util;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;


import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;


public class Renderable3D
{
    protected int GLtype;

    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData;

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 4;

    private static final int STRIDE=(POSITION_COMPONENT_COUNT+COLOR_COMPONENT_COUNT)*BYTES_PER_FLOAT;

    private int mVertexCount;
    private float[] mVertexArray;

    private int aPositionLocation;
    private int aColorLocation;
    private int uSizeLocation;
    private int uMVPMatrixLocation;

    private int Pointsize=-1;

    public Renderable3D(int _type, LinkedList<Point3D> _list, int _pointSize)
    {
        GLtype =_type;
        Pointsize=_pointSize;
        createLists(_list);
    }

    public Renderable3D(int _type, LinkedList<Point3D> _list)
    {
        GLtype =_type;
        createLists(_list);
    }

    public void createLists(LinkedList<Point3D> _list)
    {
            if (mVertexArray != null)
                mVertexArray = null;

            mVertexCount = _list.size();
            mVertexArray = new float[mVertexCount  * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)];

            int desplazamiento = 0;

            Point3D actual;
            Color color;

            for (int i = 0; i < mVertexCount; i++)
            {
                actual = _list.get(i);

                mVertexArray[desplazamiento++] = actual.getX();
                mVertexArray[desplazamiento++] = actual.getY();
                mVertexArray[desplazamiento++] = actual.getZ();

                color = actual.getColor();
                mVertexArray[desplazamiento++] = color.getValueR();
                mVertexArray[desplazamiento++] = color.getValueG();
                mVertexArray[desplazamiento++] = color.getValueB();
                mVertexArray[desplazamiento++] = color.getValueA();
            }

            if (vertexData != null)
            {
                synchronized (vertexData)
                {
                    vertexData.clear();
                    vertexData = null;
                }
            }

            vertexData = ByteBuffer
                    .allocateDirect(mVertexArray.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();

            synchronized (vertexData)
            {
                vertexData.clear();
                vertexData.put(mVertexArray);
            }
    }


    public void onSurfaceCreated(int _positionLocation, int _colorLocation, int _sizeLocation, int _MVPMatrixLocation)
    {
        aPositionLocation=_positionLocation;
        aColorLocation=_colorLocation;
        uSizeLocation=_sizeLocation;
        uMVPMatrixLocation=_MVPMatrixLocation;
    }

    private void loadBufferShader()
    {
        if (vertexData!=null)
        {
            synchronized (vertexData)
            {
                // Asociando vértices con su attribute
                vertexData.position(0);
                glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
            }
        }

        if (vertexData!=null)
        {
            synchronized (vertexData)
            {
                vertexData.position(POSITION_COMPONENT_COUNT);
                glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
            }
        }
    }


    public void draw(float[] MVP, float[] modelMatrix)
    {
        if (vertexData == null)
            return;

        loadBufferShader();


        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVP, 0);
        if(Pointsize!=-1)
            glUniform1f(uSizeLocation, Pointsize);


        glDrawArrays(GLtype, 0, mVertexCount);
    }

}
