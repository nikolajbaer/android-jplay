package com.nikolajbaer.game.objects;

/* jbox2d */
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

public class ObstacleObject extends GameObject {

    public ObstacleObject(Body b){
        super(b); 
    }

    public String getRenderKey(){ return "obstacle"; }

}
