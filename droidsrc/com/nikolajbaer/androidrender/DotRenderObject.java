package com.nikolajbaer.androidrender;

import java.lang.Math;

/* android */
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Paint;

import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.render.Renderable;

public class DotRenderObject extends AndroidRenderObject { 
    private RectF m_dotRect;
    private Paint m_jPlayPaint;

    public DotRenderObject(){
        m_dotRect=new RectF(-10,-10,20,20);
        m_jPlayPaint=new Paint();
        m_jPlayPaint.setARGB(255,255,0,0);
    }

    public void renderFromWorld(float x,float y,float a){
        m_canvas.save();
        m_canvas.translate(x*m_pixelsPerMeter,y*m_pixelsPerMeter);
        m_canvas.rotate((float)Math.toDegrees(a));
        m_canvas.drawOval(m_dotRect,m_jPlayPaint);
        m_canvas.restore();
    }
}
