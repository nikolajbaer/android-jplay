package com.nikolajbaer.game.objects;

/* jbox2d */
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

/* AWT */
//import java.awt.*;

/* local */
import com.nikolajbaer.game.Game;

public class ShrapnelObject extends PolygonGameObject {
    //private Color m_color;
    private int m_impacts;
    private int m_life;
    private int m_size;

    // TODO how do i keep the color?
    public ShrapnelObject(Body b,int size){
        super(b,new float[]{-1,-1,0,1,1,-1});
        //m_color=c;
        m_impacts=0;
        m_size=size;
        m_life=30;
    }

    public boolean survivesImpact(){
        // fade out after first impact
        m_impacts++;
        return m_impacts<=2;
    }


    public boolean doesDamage(){
        return false;
    }

    /*
    public void draw( Graphics2D g ){
        g.setColor(m_color);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        int s=(int)(m_size*(m_life/30.0f));
        g.fillOval(0,0,s,s);
        //g.fillOval(0,0,m_size,m_size);

    }
    */

    public boolean tick(){ 
        if(m_impacts>=1){
            m_life--; 
            return m_life>0;
        }
        return true;
    }

    public String getRenderKey(){ return "shrapnel"; }

}
