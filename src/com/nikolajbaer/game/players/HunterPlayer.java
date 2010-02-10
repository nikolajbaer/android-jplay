package com.nikolajbaer.game.players;


/* java */
import java.lang.Math;
import java.util.ArrayList;

/* jbox2d */
import org.jbox2d.common.Vec2;

/* local */
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.game.Game;
import com.nikolajbaer.Util;

public class HunterPlayer extends GamePlayer {
    private static final int HUNTING = 0;
    private static final int ATTACKING = 1;

    private int m_state;
    private GameObject m_target;

    private Vec2 m_targetVector;
    private Vec2 m_targetProjectedPosition;
    private float m_skill;

    public HunterPlayer(PlayerObject go,float skill){
        super(go);
        m_state=HUNTING;
        m_skill=skill;
    }

    public HunterPlayer(PlayerObject go){
        super(go);
        m_state=HUNTING;
        m_skill=0; // perfect skill
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

    // NOT WORKING
    private float getAngleToTarget(){
        // TODO make this accurate:
        // mediocre reference http://www.euclideanspace.com/maths/algebra/vectors/angleBetween/index.htm 
        Vec2 tp=m_target.getBody().getPosition(); // target positoin
        Vec2 d=tp.sub(m_playerObject.getBody().getPosition()); // me to target 
        d.normalize();
        Vec2 lv=Util.rotate(new Vec2(0,-1),m_playerObject.getBody().getAngle()); // my direction vector
        //float a=(float)(Math.acos(Vec2.dot(d,lv)));
        return Util.angleTo(d,lv);
    }

    private float getDirectionOfTarget(){
        // get current target position and velocity
        Vec2 tp=m_target.getBody().getPosition(); 
        Vec2 td=m_target.getBody().getLinearVelocity(); 
   
        // find out vector to target 
        Vec2 d=tp.sub(m_playerObject.getBody().getPosition()); // me to target 

        // and vector of my current angle
        Vec2 lv=Util.rotate(new Vec2(0,-1),m_playerObject.getBody().getAngle()); // my direction vector

        // anticipate new position 
        float v=m_playerObject.getWeaponVelocity();

        // lengthen linear velocity by distance from target over velocity of weapon 
        // to figure out new target position
        Vec2 np=tp.add(td.mul(d.length()/v));
        m_targetProjectedPosition=np;

        // now get me to new target
        Vec2 d2 = np.sub(m_playerObject.getBody().getPosition());
        m_targetVector=d2;

        // account for skill.. or rahter disrupt for incompetence
        if(m_skill>0){
            m_targetVector=m_targetVector.add(new Vec2(
                (float)(Math.random()*m_skill),
                (float)(Math.random()*m_skill)));
        } 
        // and fire at where they will be
        return Vec2.cross(d2,lv);
    }

    public void tick(){
        // lets not do anything if we are dead
        if( !m_playerObject.isAlive() ){ return; }
        switch(m_state){
            case HUNTING:
                doHunt();
                break;
            case ATTACKING:
                doAttack();
                break;
        }
    } 

    protected void doAttack(){
        // HACK should i bet targeting game object? how do i efficiently know if it is still around?
        if( m_target.getBody() == null){
            m_playerObject.triggerOff();
            m_playerObject.stopRotate();
            System.out.println("target dead, hunting..");
            m_state=HUNTING;
            return; // is this dirty?
        }

        // is target destroyed? switch to hunting, else
        // aim at target (left or right to minimize angle)
        // if angle < min, shoot 
        //float a=getAngleToTarget();
        //System.out.println("Homing: "+a+", "+getDirectionOfTarget());
        float a=getDirectionOfTarget();
        // TODO need to make this focus on visible cross section of tank with velocity prediciton
        // TODO perhaps add debugging visual overlays? so easier to see what tank is thinking
        // TODO stop shooting if target is dead
        if(a > 0.1){ 
            m_playerObject.triggerOff();
            m_playerObject.left(); 
        }else if(a < -0.1){ 
            m_playerObject.triggerOff();
            m_playerObject.right(); 
        }else{
            m_playerObject.stopRotate();
            m_playerObject.triggerOn();
            if(m_targetVector.length()>20){
                m_playerObject.forward();
            }else if(m_targetVector.length() < 5){
                m_playerObject.reverse();
            }else{
                m_playerObject.halt();
            }
        }
    }

    protected void doHunt(){
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
    }
}
