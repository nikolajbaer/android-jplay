package com.nikolajbaer.androidrender;

/* android */
import android.graphics.Canvas;

import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.render.Renderable;

public abstract class AndroidRenderObject extends RenderObject { 
    protected Canvas m_canvas; 
    protected float m_pixelsPerMeter;

    public void setPixelRatio(float ppm){
        m_pixelsPerMeter=ppm;
    }

    public void setCanvas(Canvas c){
        m_canvas=c;
    }

}
