package com.nikolajbaer.game;


/* java */
import java.lang.Math;

/* jbox2d */
import org.jbox2d.common.Vec2;

/* local */
import com.nikolajbaer.game.objects.*;



public class HunterPlayer extends GamePlayer {
    private static final int HUNTING = 0;
    private static final int ATTACKING = 1;

    private int m_state;
    private PlayerObject m_target;

    public HunterPlayer(PlayerObject go){
        super(go);
        m_state=HUNTING;
    }

    private PlayerObject acquireTarget(){
        return null;
    }

    private float getAngleToTarget(){
        // TODO make this accurate:
        // mediocre reference http://www.euclideanspace.com/maths/algebra/vectors/angleBetween/index.htm 
        Vec2 tp=m_target.getBody().getPosition();
        Vec2 d=tp.sub(m_playerObject.getBody().getPosition());
        Vec2 lv=GameObject.rotate(new Vec2(1,0),m_playerObject.getBody().getAngle());
        // figure out angle to target 
        return (float)(Math.acos(Vec2.dot(d,lv)));
    }

    public void tick(){
        switch(m_state){
            case HUNTING:
                // look for a target
                PlayerObject t=acquireTarget();
                // if target acquired, set and move to attacking
                if(t!=null){
                    m_target=t;
                    m_state=ATTACKING;
                }else{
                    // TODO check if we are on a collision course, and slow
                    // down and veer
                    // otherwise go forward
                    m_playerObject.forward();
                    double r=Math.random();
                    if( r < 0.25){ 
                        m_playerObject.left();
                    }else if(r > 0.75){
                         m_playerObject.right();
                    }else{ /* go straight */ }
                }
                // else move forward and randomly left or right
                break;
            case ATTACKING:
                // is target destroyed? switch to hunting, else
                // aim at target (left or right to minimize angle)
                // if angle < min, shoot 
                break;
        }
    } 
}
