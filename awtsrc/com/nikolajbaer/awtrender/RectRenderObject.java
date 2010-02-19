package com.nikolajbaer.awtrender;

import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.render.Renderable;

import java.awt.*;
import java.awt.geom.*;

public class RectRenderObject extends PolygonRenderObject { 
    public RectRenderObject(int width,int height){
        super();
        x_pts=new int[]{0,width,width,0};        
        y_pts=new int[]{0,0,height,height};        
        m_color=Color.gray;
    }
}
