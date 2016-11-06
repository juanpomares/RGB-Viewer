package juanpomares.rgbviewer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import juanpomares.rgbviewer.Util.Color;
import juanpomares.rgbviewer.Util.OpenGLRenderer;
import juanpomares.rgbviewer.Util.Point3D;
import juanpomares.rgbviewer.Util.Renderable3D;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;

public class MainActivity extends AppCompatActivity {

    int ValueR=0, ValueG=0, ValueB=0;
    protected GLSurfaceView glSurfaceView;
    private boolean rendererSet = false, cambio;

    protected OpenGLRenderer renderer;
    private float HistoricalNormalizedX, HistoricalNormalizedY;


    private Renderable3D myPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startListenerSeekBars();
        startOpenGLView();
    }

    private void startListenerSeekBars()
    {
        for(int i=0; i<3; i++)
        {
            SeekBar sb=(SeekBar) findViewById(getSBId(i));
            if(sb!=null)
                sb.setOnSeekBarChangeListener(changelistener);
        }
    }

    private void startOpenGLView()
    {

        glSurfaceView=(GLSurfaceView)findViewById(R.id.GLView);

        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        //final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));
        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);
            // Para que funcione en el emulador
            glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            // Assign our renderer.

            glSurfaceView.setRenderer(renderer=new OpenGLRenderer(this));

            rendererSet = true;
            /*Toast.makeText(this, "OpenGL ES 2.0 supported",
                    Toast.LENGTH_LONG).show();*/
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event != null)
                {
                    float normalizedX =   ((event.getX()) / (float) v.getWidth()) * 2 - 1;
                    float normalizedY = -(((event.getY()) / (float) v.getHeight()) * 2 - 1);

                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        final float nX =   normalizedX;
                        final float nY = normalizedY;

                        HistoricalNormalizedX =normalizedX;
                        HistoricalNormalizedY =normalizedY;
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        final float normalizedMoveX =   normalizedX- HistoricalNormalizedX;
                        final float normalizedMoveY =	normalizedY- HistoricalNormalizedY;

                        HistoricalNormalizedX =normalizedX;
                        HistoricalNormalizedY =normalizedY;

                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                renderer.handleTouchDrag(normalizedMoveX, normalizedMoveY);
                            }
                        });

                    }else if (event.getAction() == MotionEvent.ACTION_UP)
                    {

                    }

                    return true;
                } else {
                    return false;
                }
            }
        });

        LinkedList<Point3D> aux=new LinkedList<Point3D>();

        aux.addLast(crearRGBPointColor(0, 0, 0, 1));
        aux.addLast(crearRGBPointColor(0, 0, 1, 1));
        aux.addLast(crearRGBPointColor(0, 1, 1, 1));
        aux.addLast(crearRGBPointColor(0, 1, 0, 1));
        aux.addLast(crearRGBPointColor(1, 1, 0, 1));
        aux.addLast(crearRGBPointColor(1, 0, 0, 1));
        aux.addLast(crearRGBPointColor(1, 0, 1, 1));
        aux.addLast(crearRGBPointColor(0, 0, 1, 1));

        renderer.AddRenderable(new Renderable3D(GL_TRIANGLE_FAN, aux));


        aux.clear();
        aux.add(0, new Point3D(ValueR/255f, ValueG/255f, ValueB/255f, new Color(ValueR, ValueG, ValueB)));


        renderer.AddRenderable(myPoint=new Renderable3D(GL_POINTS, aux, 10));

       /* aux.add(auxInt++, crearRGBPointColor(0, 0, 1, 1));
        aux.add(auxInt++, crearRGBPointColor(0, 1, 1, 1));
        aux.add(auxInt++, crearRGBPointColor(0, 0, 0, 1));

        aux.add(auxInt++, crearRGBPointColor(0, 1, 0, 1));
        aux.add(auxInt++, crearRGBPointColor(1, 0, 0, 1));
        aux.add(auxInt++, crearRGBPointColor(1, 1, 0, 1));


        renderer.AddRenderable(new Renderable3D(GL_TRIANGLE_STRIP, aux));

        aux.clear();
        auxInt=0;
        aux.add(auxInt++, crearRGBPointColor(0, 0, 1, 1));

        aux.add(auxInt++, crearRGBPointColor(0, 0, 0, 1));
        aux.add(auxInt++, crearRGBPointColor(1, 0, 1, 1));
        aux.add(auxInt++, crearRGBPointColor(1, 0, 0, 1));


        renderer.AddRenderable(new Renderable3D(GL_TRIANGLE_STRIP, aux));*/

    }

    private Point3D crearRGBPointColor(float r, float g, float b, float a)
    {
        return new Point3D(r, g, b, new Color(r, g, b, a));
    }

    private SeekBar.OnSeekBarChangeListener changelistener=new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {changedSeekbar(seekBar); }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    };

    private void changedSeekbar(SeekBar seekbar)
    {
        Point3D x=new Point3D();
        int progress=Math.min(seekbar.getProgress(), 255);
        TextView tv=null;
        switch (seekbar.getId())
        {
            case R.id.SBR:
                if(progress!=ValueR)
                {
                    tv=(TextView) findViewById(R.id.valueR);
                    ValueR=progress;
                }
                break;
            case R.id.SBG:
                if(progress!=ValueR)
                {
                    tv=(TextView) findViewById(R.id.valueG);
                    ValueG=progress;
                }
                break;
            default: /*R.id.SBB;*/
                if(progress!=ValueR)
                {
                    tv=(TextView) findViewById(R.id.valueB);
                    ValueB=progress;
                }
                break;
        }

        if(tv!=null)
        {
            LinkedList<Point3D> aux=new LinkedList<Point3D>();
            aux.addLast(new Point3D(ValueR/255f, ValueG/255f, ValueB/255f, new Color(ValueR, ValueG, ValueB)));
            myPoint.createLists(aux);
            tv.setText(progress+"");
        }
    }

    private int getSBId(int num)
    {
        switch (num)
        {
            case 0:
                return R.id.SBR;
            case 1:
                return R.id.SBG;
            default:
                return R.id.SBB;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}
