package com.nikolajbaer.game.objects;

/* jbox2d */
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

/* AWT */
import java.awt.*;

/* local */
import com.nikolajbaer.game.Game;

public class ShrapnelObject extends PolygonGameObject {
    private Color m_color;
    private int impacts;

    public ShrapnelObject(Body b,Color c){
        super(b,new float[]{-1,-1,0,1,1,-1});
        m_color=c;
        impacts=0;
    }

    public boolean survivesImpact(){
        impacts++;
        return impacts <= 0; // die on first impact (otherwise would be nice to die off after a time..
    }

    public boolean doesDamage(){
        return false;
    }

    public void draw( Graphics2D g ){
        g.setColor(m_color);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        g.fillOval(0,0,2,2);
    }


}
