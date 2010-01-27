package com.nikolajbaer.androidrender;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.content.Context;
import android.util.AttributeSet;

class JPlayView extends SurfaceView implements SurfaceHolder.Callback {
    private JPlayThread thread;

    class JPlayThread extends Thread { 
        private boolean mRun;
        private RectF mScreenRect;
        private Paint mJPlayPaint;
        private SurfaceHolder mSurfaceHolder;

        public JPlayThread(SurfaceHolder surfaceHolder){
            mSurfaceHolder=surfaceHolder;
            mRun=true;
            mJPlayPaint = new Paint();
            mJPlayPaint.setARGB(255,  255, 0, 0);
            mScreenRect = new RectF(0,0,0,0);
        }

        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mScreenRect = new RectF(0,0,width,height);
            }
        }

        @Override
        public void run() {
            int i=0;
            while(mRun){
                Canvas c = null;
                i++; 

                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        c.drawRect(mScreenRect, mJPlayPaint);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            
                try {
                    Thread.sleep(100);
                } catch( InterruptedException ie ){
                }
            }
        }

        public void setRunning(boolean b) {
            mRun = b;
        }

    } // inner class JPlayThread


    public JPlayView(Context context, AttributeSet attrs){
        super(context,attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        thread = new JPlayThread(holder); 
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

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

}
