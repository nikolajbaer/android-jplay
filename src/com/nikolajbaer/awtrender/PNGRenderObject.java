package com.nikolajbaer.awtrender;

import com.nikolajbaer.render.RenderObject;
import com.nikolajbaer.render.Renderable;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class PNGRenderObject extends AWTRenderObject { 
    private Image m_image;
    private final static Color CLEAR=new Color(0,0,0,0);
    private int m_xpos;
    private int m_ypos;

    public PNGRenderObject(String pngFile){
        // TODO load pngFile
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // TODO add try/except
        m_image = toolkit.getImage(pngFile);
        int w=m_image.getWidth(null);
        int h=m_image.getHeight(null);
        m_xpos=-w/2;
        m_ypos=-h;
    }

    public void renderFromWorld(float x,float y,float a){
        int w=m_image.getWidth(null);
        int h=m_image.getHeight(null);
        System.out.println("I am "+w+"x"+h+" pixels, or "+(w/m_pixelsPerMeter)+"x"+(h/m_pixelsPerMeter)+ " meters");
        m_graphics.translate(x*m_pixelsPerMeter,y*m_pixelsPerMeter);
        m_graphics.rotate(a);
        m_graphics.drawImage(m_image,m_xpos,m_ypos,null);
    }

}
