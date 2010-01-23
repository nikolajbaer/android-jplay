package com.nikolajbaer.game.objects;

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

    public boolean survivesImpact(){
        return false;
    }

    public boolean doesDamage(){
        return true;
    }

    public String getRenderKey(){ return "bullet"; }

}
