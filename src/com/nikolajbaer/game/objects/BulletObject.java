package com.nikolajbaer.game.objects;

/* java */
//import java.awt.*;

/* jbox2d */
import org.jbox2d.dynamics.Body; 
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

/* local */
import com.nikolajbaer.game.Game;

public class BulletObject extends GameObject {

    public BulletObject(Body b,int damage){
        super(b);
        this.m_damage=damage;
    }

    /*
    public void draw( Graphics2D g ){
        g.setColor(Color.white);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        //g.rotate(m_body.getAngle());
        //Vec2 d=Game.toScreen(getDir());
        //int mp=(int)(pixelsPerMeter);
        //g.fillRect(-mp,-mp,mp*2,mp*2);
        int w=(int)(0.5*Game.PPM);
        g.fillOval(0,0,w,w);
    }
    */

    public boolean survivesImpact(){
        return false;
    }

    public boolean doesDamage(){
        return true;
    }

    public String getRenderKey(){ return "bullet"; }

}
