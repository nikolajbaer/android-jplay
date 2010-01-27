package com.nikolajbaer.awtrender;

import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.render.Renderable;

import java.awt.*;
import java.awt.geom.*;

public class PolygonRenderObject extends AWTRenderObject { 
    private int[] x_pts;
    private int[] y_pts;
    private Color m_color;

    // TODO make this create from a key
    public PolygonRenderObject(){
        x_pts=new int[]{0,20,20,0};        
        y_pts=new int[]{0,0,20,20};        
        m_color=Color.gray;
    }

    public void renderFromWorld(float x,float y,float a){
        m_graphics.setColor(m_color);
        m_graphics.translate(x*m_pixelsPerMeter,y*m_pixelsPerMeter);
        m_graphics.rotate(a);
        m_graphics.fillPolygon(x_pts,y_pts,x_pts.length);
    }

}
