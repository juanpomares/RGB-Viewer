package juanpomares.rgbviewer;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import juanpomares.rgbviewer.Util.OpenGLRenderer;

public class MainActivity extends AppCompatActivity {

    private int mValueR =0, mValueG =0, mValueB =0;
    private GLSurfaceView mGLSurfaceView;
    private boolean mRendererSet = false;

    private OpenGLRenderer mGLRenderer;
    private float mHistoricalNormalizedX, mHistoricalNormalizedY;

    private RenderableObjects m3DObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startOpenGLView();
        m3DObjects=new RenderableObjects(mGLRenderer);

        startListenerSeekBars();
        startListenersCheckbox();
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

    private void startListenersCheckbox()
    {
        boolean VisibilityPlanes=false;
        for(int i=0; i<3; i++)
        {
            CheckBox cb=(CheckBox) findViewById(getCBId(i));
            if(cb!=null)
            {
                cb.setChecked(VisibilityPlanes);
                m3DObjects.changeState(VisibilityPlanes, i);
                cb.setOnCheckedChangeListener(mCheckedChanged);
            }
        }
    }

    private void startOpenGLView()
    {
        mGLSurfaceView =(GLSurfaceView)findViewById(R.id.GLView);

        final ActivityManager   activityManager   = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        //final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2)
        {
            // Request an OpenGL ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
            // Assign our mGLRenderer.
            mGLSurfaceView.setRenderer(mGLRenderer=new OpenGLRenderer(this));

            mRendererSet = true;
        } else
        {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show();
            return;
        }

        mGLSurfaceView.setOnTouchListener(mGLViewTouchListener);
    }

    private View.OnTouchListener mGLViewTouchListener=new View.OnTouchListener()
    {
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

                    mHistoricalNormalizedX =normalizedX;
                    mHistoricalNormalizedY =normalizedY;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    final float normalizedMoveX =   normalizedX- mHistoricalNormalizedX;
                    final float normalizedMoveY =	normalizedY- mHistoricalNormalizedY;

                    mHistoricalNormalizedX =normalizedX;
                    mHistoricalNormalizedY =normalizedY;

                    mGLSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mGLRenderer.handleTouchDrag(normalizedMoveX, normalizedMoveY);
                        }
                    });

                }else if (event.getAction() == MotionEvent.ACTION_UP){}

                return true;
            } else { return false; }
        }
    };

    CompoundButton.OnCheckedChangeListener mCheckedChanged=new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton compoundButton, final boolean b)
        {
            mGLSurfaceView.queueEvent(new Runnable()
            {
                @Override
                public void run() { m3DObjects.changeState(b, getNumByCBId(compoundButton.getId())); }
            });
        }
    };

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
        int progress=Math.min(seekbar.getProgress(), 255);
        int TextViewId=-1;

        switch (seekbar.getId())
        {
            case R.id.SBR:
                if(progress!= mValueR) { TextViewId=R.id.valueR;  mValueR =progress; }
                break;
            case R.id.SBG:
                if(progress!= mValueR) { TextViewId=R.id.valueG; mValueG =progress; }
                break;
            case R.id.SBB:
                if(progress!= mValueR) { TextViewId=R.id.valueB; mValueB =progress; }
                break;
        }

        if(TextViewId!=-1)
        {
            ((TextView) findViewById(TextViewId)).setText(progress+"");
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    m3DObjects.changeValues(mValueR/255f, mValueG/255f, mValueB/255f);
                }
            });
        }
    }

    private int getSBId(int num)
    {
        switch (num)
        {
            case 0:     return R.id.SBR;
            case 1:     return R.id.SBG;
            default:    return R.id.SBB;
        }
    }

    private int getCBId(int num)
    {
        switch (num)
        {
            case 0:     return R.id.CBR;
            case 1:     return R.id.CBG;
            default:    return R.id.CBB;
        }
    }

    private int getNumByCBId(int num)
    {
        switch (num)
        {
            case R.id.CBR:      return 0;
            case R.id.CBG:      return 1;
            default:            return 2;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRendererSet) { mGLSurfaceView.onPause(); }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRendererSet) { mGLSurfaceView.onResume(); }
    }
}
