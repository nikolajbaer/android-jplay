package com.nikolajbaer.game;


/* java */
import java.lang.Math;
import java.util.ArrayList;

/* jbox2d */
import org.jbox2d.common.Vec2;

/* local */
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.Util;

public class HunterPlayer extends GamePlayer {
    private static final int HUNTING = 0;
    private static final int ATTACKING = 1;

    private int m_state;
    private GameObject m_target;

    public HunterPlayer(PlayerObject go){
        super(go);
        m_state=HUNTING;
    }

    private GameObject acquireTarget(){
        ArrayList<GamePlayer> pos=Game.game.getPlayers();
        GameObject t=null;
        float min=100000;
        for(int i=0;i<pos.size();i++){
            GamePlayer po=pos.get(i);
            GameObject go=po.getGameObject();
            // CONSIDER more explicitely stating a playre is out than checking for a null body
            if(po != this && go.getBody() != null){
                float d=go.getBody().getPosition().sub(m_playerObject.getBody().getPosition()).length();
                if(d<min){
                    min=d;
                    t=go;
                }
            } 
        }
        return t;
    }

    private float getAngleToTarget(){
        // TODO make this accurate:
        // mediocre reference http://www.euclideanspace.com/maths/algebra/vectors/angleBetween/index.htm 
        Vec2 tp=m_target.getBody().getPosition();
        Vec2 d=tp.sub(m_playerObject.getBody().getPosition());
        d.normalize();
        Vec2 lv=Util.rotate(new Vec2(0,-1),m_playerObject.getBody().getAngle());
        float a=(float)(Math.acos(Vec2.dot(d,lv)));
        //System.out.println("Target is at "+tp+", I am at "+m_playerObject.getBody().getPosition()+" and the vec to the target is "+d+", and i am pointing in "+lv+" so d (dot) lv is "+Vec2.dot(d,lv));
        // figure out angle to target 
        return a;
    }

    public void tick(){
        switch(m_state){
            case HUNTING:
                // look for a target
                GameObject t=acquireTarget();
                // if target acquired, set and move to attacking
                if(t!=null){
                    System.out.println("target acquired: "+t);
                    m_target=t;
                    m_state=ATTACKING;
                }else{
                    // TODO check if we are on a collision course, and slow
                    // down and veer
                    // otherwise go forward
                    m_playerObject.forward();
                    double r=Math.random();
                    if( r < -0.05){ 
                        m_playerObject.left();
                    }else if(r > 0.05){
                         m_playerObject.right();
                    }else{ /* go straight */ }
                }
                // else move forward and randomly left or right
                break;
            case ATTACKING:
                // HACK should i bet targeting game object? how do i efficiently know if it is still around?
                if( m_target.getBody() == null){
                    m_state=HUNTING;
                    break; // is this dirty?
                }

                // is target destroyed? switch to hunting, else
                // aim at target (left or right to minimize angle)
                // if angle < min, shoot 
                float a=getAngleToTarget();
                System.out.println("Homing: "+a);
                // TODO need to make this focus on visible cross section of tank with velocity prediciton
                // TODO perhaps add debugging visual overlays? so easier to see what tank is thinking
                // TODO stop shooting if target is dead
                if(a > 0.05){ 
                    m_playerObject.triggerOff();
                    m_playerObject.left(); 
                }else if(a < -0.05){ 
                    m_playerObject.triggerOff();
                    m_playerObject.right(); 
                }else{
                    m_playerObject.stopRotate();
                    m_playerObject.triggerOn();
                }
                break;
        }
    } 
}
