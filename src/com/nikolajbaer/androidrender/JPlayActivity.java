package com.nikolajbaer.androidrender;

import android.view.Window;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class JPlayActivity extends Activity
{
    private Button mForward;
    private Button mReverse;
    private Button mLeft;
    private Button mRight;
    private Button mFireA;
    private Button mFireB;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        mForward = (Button)findViewById(R.id.forwardButton);
        mReverse = (Button)findViewById(R.id.reverseButton);
        mLeft = (Button)findViewById(R.id.leftButton);
        mRight = (Button)findViewById(R.id.rightButton);
        mFireA = (Button)findViewById(R.id.fireAButton);
        mFireB = (Button)findViewById(R.id.fireBButton);

    }
}
