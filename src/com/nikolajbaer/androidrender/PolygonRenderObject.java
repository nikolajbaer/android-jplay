package com.nikolajbaer.androidrender;

import java.lang.Math;

/* android */
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Path;

import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.render.Renderable;

public class PolygonRenderObject extends AndroidRenderObject { 
    private RectF m_dotRect;
    private Paint m_jPlayPaint;
    private Path m_path;

    public PolygonRenderObject(Path p,int c){
        m_jPlayPaint=new Paint();
        m_path=p;
        m_jPlayPaint.setColor(c);
    }

    public void renderFromWorld(float x,float y,float a){
        m_canvas.save();
        m_canvas.translate(x*m_pixelsPerMeter,y*m_pixelsPerMeter);
        m_canvas.rotate((float)Math.toDegrees(a));
        // TODO render polygon
        m_canvas.drawPath(m_path,m_jPlayPaint);
        m_canvas.restore();
    }
}
