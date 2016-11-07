package juanpomares.rgbviewer;

import java.util.LinkedList;

import juanpomares.rgbviewer.Util.Color;
import juanpomares.rgbviewer.Util.OpenGLRenderer;
import juanpomares.rgbviewer.Util.Point3D;
import juanpomares.rgbviewer.Util.Renderable3D;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;

/**
 * Created by Usuario on 05/11/2016.
 */

public class RenderableObjects
{
    private OpenGLRenderer mRenderer;
    private float mValueR=0, mValueG=0, mValueB=0;

    private Renderable3D mColoredCube;
    private Renderable3D mPoint;
    private Renderable3D mBorderPoint;
    private Renderable3D mLines;
    private Renderable3D mPointsLines;
    private LinkedList<Point3D> mLinkedList;

    public RenderableObjects(OpenGLRenderer _renderer)
    {
        mRenderer=_renderer;
        mLinkedList=new LinkedList<Point3D>();
        initializeObjects();
    }

    public void initializeObjects()
    {
        /*Creating cube*/
        mLinkedList.clear();
        mLinkedList.addLast(createRGBPointColor(0, 0, 0, 0.75f));
        mLinkedList.addLast(createRGBPointColor(0, 0, 1, 0.75f));
        mLinkedList.addLast(createRGBPointColor(0, 1, 1, 0.75f));
        mLinkedList.addLast(createRGBPointColor(0, 1, 0, 0.75f));
        mLinkedList.addLast(createRGBPointColor(1, 1, 0, 0.75f));
        mLinkedList.addLast(createRGBPointColor(1, 0, 0, 0.75f));
        mLinkedList.addLast(createRGBPointColor(1, 0, 1, 0.75f));
        mLinkedList.addLast(createRGBPointColor(0, 0, 1, 0.75f));

        mRenderer.AddRenderable(mColoredCube=new Renderable3D(GL_TRIANGLE_FAN, mLinkedList));


        /*creating point*/
        fillLinkedListMainPoint();
        mRenderer.AddRenderable(mPoint=new Renderable3D(GL_POINTS, mLinkedList, 15));

        /*creating border point*/
        fillLinkedListBorderPoint();
        mRenderer.AddRenderable(mBorderPoint=new Renderable3D(GL_POINTS, mLinkedList, 20));

        fillLinkedListLines();
        mRenderer.AddRenderable(mLines=new Renderable3D(GL_LINES, mLinkedList));
        mRenderer.AddRenderable(mPointsLines=new Renderable3D(GL_POINTS, mLinkedList, 10));


    }

    private void fillLinkedListMainPoint()
    {
        mLinkedList.clear();
        mLinkedList.addLast(createRGBPointColor(mValueR, mValueG, mValueB, 1));
    }

    private void fillLinkedListBorderPoint()
    {
        mLinkedList.clear();
        mLinkedList.addLast(createInverseRGBPointColor(mValueR, mValueG, mValueB, 1));
    }

    private void fillLinkedListLines()
    {
        mLinkedList.clear();
        mLinkedList.addLast(createRGBPointColor(0, mValueG, mValueB, 1));
        mLinkedList.addLast(createRGBPointColor(1, mValueG, mValueB, 1));

        mLinkedList.addLast(createRGBPointColor(mValueR, mValueG, 0, 1));
        mLinkedList.addLast(createRGBPointColor(mValueR, mValueG, 1, 1));

        mLinkedList.addLast(createRGBPointColor(mValueR, 0, mValueB, 1));
        mLinkedList.addLast(createRGBPointColor(mValueR, 1, mValueB, 1));
    }

    private Point3D createRGBPointColor(float r, float g, float b, float a) { return new Point3D(r, g, b, new Color(r, g, b, a)); }
    private Point3D createInverseRGBPointColor(float r, float g, float b, float a) { return new Point3D(r, g, b, new Color(1-r, 1-g, 1-b, a)); }

    public void changeValues(float newR, float newG, float newB)
    {
        mValueR=newR; mValueG=newG; mValueB=newB;


        fillLinkedListMainPoint();
        mPoint.createLists(mLinkedList);

        fillLinkedListBorderPoint();
        mBorderPoint.createLists(mLinkedList);

        fillLinkedListLines();
        mLines.createLists(mLinkedList);
        mPointsLines.createLists(mLinkedList);

    }
}
