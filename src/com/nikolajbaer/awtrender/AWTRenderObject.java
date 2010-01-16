package com.nikolajbaer.awtrender;

import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.render.Renderable;

import java.awt.*;
import java.awt.geom.*;

public abstract class AWTRenderObject extends RenderObject { 
    protected Graphics2D m_graphics; 
    protected float m_pixelsPerMeter;

    public void setPixelRatio(float ppm){
        m_pixelsPerMeter=ppm;
    }

    public void setGraphics(Graphics2D g){
        m_graphics=g;
    }

}
