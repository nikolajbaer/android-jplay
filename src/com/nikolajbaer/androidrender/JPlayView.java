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

/* jbox2d */
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/* local */
import com.nikolajbaer.render.Renderable;
import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.Util;
import com.nikolajbaer.game.*;


class JPlayView extends SurfaceView implements SurfaceHolder.Callback {
    private JPlayThread thread;

    class JPlayThread extends Thread { 
        private static final String TAG = "JPlayThread";

        private boolean mRun;
        private RectF mScreenRect;
        private Paint mJPlayPaint;
        private SurfaceHolder mSurfaceHolder;
   
        /* rendering tools */ 
        private HashMap<String,AndroidRenderObject> m_renderObjects;
        private HashMap<Integer,Boolean> m_keyMap;

        /* game items */
        private Game m_game;
        public static final float PPM = 10.0f;
        private int m_gameWidth=200;
        private int m_gameHeight=200;

        public JPlayThread(SurfaceHolder surfaceHolder){
            mSurfaceHolder=surfaceHolder;
            mRun=true;
            mJPlayPaint = new Paint();
            mJPlayPaint.setARGB(255,  255, 0, 0);
            mScreenRect = new RectF(0,0,0,0);

            // init game
            m_renderObjects=new HashMap<String,AndroidRenderObject>();
            int gwidth=(int)(m_gameWidth/PPM);
            int gheight=(int)(m_gameHeight/PPM);
            m_game=new Game(gwidth,gheight);
            Game.game=m_game;

            // Add Players
            float[] verts={-2.4f,3.2f,-2.4f,-3.2f,2.4f,-3.2f,2.4f,3.2f};
    
            // Spread the players out in a ring 
            Vec2 mid=new Vec2(gwidth/2.0f,gheight/2.0f);
            Vec2 offset=new Vec2(gwidth/2*0.75f,0);
            int np=2; //6;
            for(int i=0;i<np;i++){
                Body b=m_game.createRect(0.2f,-2.4f,-3.2f,4.8f,6.4f);
                //Body b=m_game.createPolygon(1.0f, verts );
    
                double a=(2*Math.PI)/np;
                Vec2 rv=Util.rotate( offset, (float)(i*a) );
                float ra=(float)(i*a < Math.PI ? i*a+Math.PI : i*a-Math.PI);
                b.setXForm( mid.add(rv) ,(float)(ra+Math.PI/2)); // i guess it goes from 0,1 not 1,0
                PlayerObject po=new PlayerObject(b,verts);
    
                if(false){ //i==0){ 
                    m_game.addPlayer(new LivePlayer(po),true);
                }else{
                    m_game.addPlayer(new HunterPlayer(po));
                }
            }
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

                // tick then render
                //Log.v(TAG, "ticking");
                m_game.tick();                

                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        //Log.v(TAG, "rendering");
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
        
                /*    
                try {
                    Thread.sleep(100);
                } catch( InterruptedException ie ){
                }*/
            }
        }

        private void doDraw(Canvas c){
            ArrayList<Renderable> renderables=m_game.getRenderables();
            Log.v(TAG, "rendering "+renderables.size()+" objects");

            for(int i=0;i<renderables.size(); i++){
                Renderable r=renderables.get(i);
                AndroidRenderObject ro=(AndroidRenderObject)r.getRenderObject();
                if(ro==null){
                    String k=r.getRenderKey();
                    ro=m_renderObjects.get(k);
                    if(ro==null){
                        // TODO create render object
                        //ro=new PNGRenderObject("media/"+k+".png");
                        ro=new DotRenderObject();
                        m_renderObjects.put(k,ro);
                    }
                }
                float[] wt=r.getWorldTransform();
                ro.setCanvas(c);
                ro.setPixelRatio(PPM);
                ro.renderFromWorld(wt[0],wt[1],wt[2]);
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
