package com.nikolajbaer.androidrender;

import java.lang.Math;

/* android */
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.render.Renderable;

public class DrawableRenderObject extends AndroidRenderObject { 
    private Drawable m_drawable;
    
    public DrawableRenderObject(Drawable drawable){
        m_drawable=drawable;
    }

    public void renderFromWorld(float x,float y,float a){
        m_canvas.save();
        m_canvas.translate(x*m_pixelsPerMeter,y*m_pixelsPerMeter);
        m_canvas.rotate((float)Math.toDegrees(a));
        // draw something
        int w=m_drawable.getIntrinsicWidth();
        int h=m_drawable.getIntrinsicHeight();
        m_drawable.setBounds(-w/2,-h/2,w/2,h/2);
        m_drawable.draw(m_canvas);
        m_canvas.restore();
    }
}
