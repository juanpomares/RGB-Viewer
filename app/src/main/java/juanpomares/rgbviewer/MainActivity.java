package juanpomares.rgbviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int ValueR=0, ValueG=0, ValueB=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startListenerSeekBars();
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
}
