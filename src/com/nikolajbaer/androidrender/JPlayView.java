package com.nikolajbaer.androidrender;

/* java */
import java.util.ArrayList;
import java.util.HashMap;

/* android */
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

import android.view.KeyEvent;

/* jbox2d */
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/* local */
import com.nikolajbaer.render.Renderable;
import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.Util;
import com.nikolajbaer.game.*;
import com.nikolajbaer.androidrender.R;


public class JPlayView extends SurfaceView implements SurfaceHolder.Callback {
    private JPlayThread thread;
    private GestureDetector gestureDetector;

    public JPlayView(Context context, AttributeSet attrs){
        super(context,attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new JPlayThread(holder,context); 

        gestureDetector = new GestureDetector(new JPlayGestureDetector());

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v,int keyCode,KeyEvent event) {
                if(event.getAction()==KeyEvent.ACTION_DOWN){
                    Log.v("Gesture","Key Down "+event);
                    thread.triggerOn();
                }else{
                    Log.v("Gesture","Key Up "+event);
                    thread.triggerOff();
                }
                return false;
            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v,MotionEvent event) {
                Log.v("Touchy","at "+event.getX()+","+event.getY());
                if(event.getAction() == MotionEvent.ACTION_UP){
                    Log.v("Touchy","at "+event.getX()+","+event.getY());
                }
                thread.setPlayerAim(event.getX(),event.getY());
                //return gestureDetector.onTouchEvent(event);
                return false;
            }
        });

        // TODO add click listener, and drive tank toward click (for now)
        requestFocus();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void processFling(float vx,float vy){
        Log.v("Gesture","on fling "+vx+"x"+vy);
        thread.setPlayerAim(vx,vy);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    class JPlayGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            processFling(velocityX,velocityY);
            return false;
        }
    }

    
}
