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

        mView = (JPlayView)findViewById(R.id.jplay);

        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

        // TODO handle phones without accelerometers
        mSensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(listener,
                                        mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                                        SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(listener);
    }

    private SensorEventListener listener=new SensorEventListener() {
        public void onSensorChanged(SensorEvent e) {
            if (e.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
                Log.v("Sensors","Got "+e.values[0]+","+e.values[1]);
            }
        }
 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // unused
        }
    };

}
