package com.nikolajbaer.androidrender;

import android.view.Window;
import android.app.Activity;
import android.os.Bundle;

public class JPlayActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
    }
}
