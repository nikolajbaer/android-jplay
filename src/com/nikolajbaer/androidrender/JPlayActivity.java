package com.nikolajbaer.androidrender;

import android.view.Window;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

public class JPlayActivity extends Activity
{
    private JPlayView mView;

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
