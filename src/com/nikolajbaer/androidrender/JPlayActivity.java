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

public class JPlayActivity extends Activity
{
    private JPlayView mView;
    //private GestureDetector gestureDetector;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        mView = (JPlayView)findViewById(R.id.jplay);

    }

}
