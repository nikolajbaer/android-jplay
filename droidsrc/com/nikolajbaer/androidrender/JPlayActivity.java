package com.nikolajbaer.androidrender;

import android.util.Log;
import android.view.Window;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.content.Context;
import android.content.pm.ActivityInfo;

public class JPlayActivity extends Activity
{
    private JPlayView mView;
    private SensorManager mSensorManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mView = (JPlayView)findViewById(R.id.jplay);

        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

        // TODO handle phones without accelerometers
        mSensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(listener,
                                        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                        SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(listener);
    }

    private SensorEventListener listener=new SensorEventListener() {
        public void onSensorChanged(SensorEvent e) {
            //Log.v("MySensors","Got "+e.values[0]+" and "+e.values[1]);
            float x=e.values[0]/5.0f;
            float y=e.values[1]/5.0f;
            if(x>1.0f){ x=1.0f; }else if(x<-1.0f){ x=-1.0f; }
            if(y>1.0f){ y=1.0f; }else if(y<-1.0f){ y=-1.0f; }
            mView.applyTilt(x,y);
        }
 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // unused
        }
    };

}
