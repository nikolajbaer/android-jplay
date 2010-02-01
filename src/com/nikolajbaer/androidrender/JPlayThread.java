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


public class JPlayThread extends Thread { 
    private static final String TAG = "JPlayThread";
    
    private boolean mRun;
    private RectF mScreenRect;
    private Paint mClearPaint;
    private SurfaceHolder mSurfaceHolder;
    private Context mContext;
    
    /* rendering tools */ 
    private HashMap<String,AndroidRenderObject> m_renderObjects;
    private HashMap<Integer,Boolean> m_keyMap;
    
    /* game items */
    private Game m_game;
    public static final float PPM = 10.0f;
    // TODO feed this from what is determined on creation
    private int m_gameWidth=320;
    private int m_gameHeight=350;
    
    public JPlayThread(SurfaceHolder surfaceHolder,Context context){
        mSurfaceHolder=surfaceHolder;
        mContext = context;
        mRun=true;
        mClearPaint = new Paint();
        mClearPaint.setARGB(255,  0, 0, 0);
        mScreenRect = new RectF(0,0,0,0);
    
        // init game
        m_renderObjects=new HashMap<String,AndroidRenderObject>();
        int gwidth=(int)(toWorld(m_gameWidth));
        int gheight=(int)(toWorld(m_gameHeight));
        m_game=new Game(gwidth,gheight);
        Game.game=m_game;
    
        // Add Players
        float[] verts={-2.4f,3.2f,-2.4f,-3.2f,2.4f,-3.2f,2.4f,3.2f};
    
        // Spread the players out in a ring 
        Vec2 mid=new Vec2(gwidth/2.0f,gheight/2.0f);
        Vec2 offset=new Vec2(gwidth/2*0.75f,0);
        int np=2;//2; //6;
        for(int i=0;i<np;i++){
            Body b=m_game.createRect(0.2f,-2.4f,-3.2f,4.8f,6.4f);
            //Body b=m_game.createPolygon(1.0f, verts );
    
            double a=(2*Math.PI)/np;
            Vec2 rv=Util.rotate( offset, (float)(i*a) );
            float ra=(float)(i*a < Math.PI ? i*a+Math.PI : i*a-Math.PI);
            b.setXForm( mid.add(rv) ,(float)(ra+Math.PI/2)); // i guess it goes from 0,1 not 1,0
            PlayerObject po=new PlayerObject(b,verts);
    
            if(i==0){ 
                m_game.addPlayer(new LivePlayer(po),true);
            }else{
                m_game.addPlayer(new HunterPlayer(po));
            }
        }
    }


    /* Callback invoked when the surface dimensions change. */
    public void setSurfaceSize(int width, int height) {
        Log.v(TAG,"Surface size: "+width+"x"+height);
        // synchronized to make sure these all change atomically
        synchronized (mSurfaceHolder) {
            mScreenRect = new RectF(0,0,width,height);
        }
    }

    public void doKeys(int[] keys){
        // TODO update internal key map
    }
    
    @Override
    public void run() {
        int i=0;
        while(mRun){
            Canvas c = null;
            i++; 
    
            // tick then render
            //Log.v(TAG, "ticking");
            // TODO process key map for player actions
            synchronized(m_game){
                m_game.tick();
            }
    
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
        }
    }
    
    private void doDraw(Canvas c){
        // TODO do dirty rect drawing
        c.drawRect(mScreenRect,mClearPaint);
    
        ArrayList<Renderable> renderables=m_game.getRenderables();
        //Log.v(TAG, "rendering "+renderables.size()+" objects");
    
        for(int i=0;i<renderables.size(); i++){
            Renderable r=renderables.get(i);
            AndroidRenderObject ro=(AndroidRenderObject)r.getRenderObject();
            if(ro==null){
                String k=r.getRenderKey();
                ro=m_renderObjects.get(k);
                if(ro==null){
                    Log.v(TAG," loading renderable "+k);
                    // TODO create render object
                    //ro=new PNGRenderObject("media/"+k+".png");
                    //Drawable img = context.getResources().getDrawable(R.drawable.lander_crashed);
                    // TODO find out how to use string lookups on Drawable resources
                    int h=R.drawable.bullet;
                    if(k=="tank"){
                        h=R.drawable.tank;
                    }
                    //ro=new DotRenderObject();
                    Drawable img = mContext.getResources().getDrawable(h);
                    ro=new DrawableRenderObject(img);
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

    /* Convert world/screen */
    private float[] toWorld(float[] n){
        if(n==null){ return null; }
        float[] r=new float[n.length];
        for(int i=0;i<n.length;i++){
            r[i]=toWorld(n[i]);
        }
        return r;
    }

    private float[] toScreen(float[] n){
        if(n==null){ return null; }
        float[] r=new float[n.length];
        for(int i=0;i<n.length;i++){
            r[i]=toWorld(n[i]);
        }
        return r;
    }

    private float toWorld(float n){
        return n/PPM;
    }

    private float toScreen(float n){
        return n*PPM;
    }
    
    public void triggerOn(){
        synchronized(m_game){
            m_game.getPlayer().triggerOn();
        }
    }

    public void triggerOff(){
        synchronized(m_game){
            m_game.getPlayer().triggerOff();
        }
    }

    // incoming as screen coords
    public void setPlayerAim(float x,float y){
        synchronized(m_game){
            //m_game.getPlayer().-
        }
    }
    
}


