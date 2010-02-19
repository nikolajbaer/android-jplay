package com.nikolajbaer.game.weapons;

/* jbox2d */
import org.jbox2d.dynamics.Body; 
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

/* local */
import com.nikolajbaer.game.*;
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.Util;

public class Blaster extends Weapon {
    private boolean m_shooting;
    private static float m_bulletVelocity = 20.0f;
    private static final int PRIME_COUNT=10;
    private static final int RELOAD_COUNT=10;
    private int m_flightTime;
    private int m_reloadTime;
    private GameObject m_blast;

    public Blaster(){
        m_shooting=false;
        m_blast=null;
    }

    public void triggerOn(){
        m_shooting=true;
    }

    public void triggerOff(){
       m_shooting=false;
    }

    public void tick(PlayerObject shooter){
        //System.out.println("ticking");
        m_flightTime++;
        m_reloadTime++;
        if(m_shooting && m_blast == null && m_reloadTime > RELOAD_COUNT){
            System.out.println("shoot");
            shooter.drawEnergy(60);
            // TODO make it so the shooter can't get hit by their own bullets?
            Vec2 d=shooter.getDir();
            Body b=Game.game.createCircle(1.0f,0.3f);
            BulletObject bo=new BulletObject(b,10);
            b.setXForm(shooter.getBody().getWorldCenter().add(d.mul(4)),shooter.getBody().getAngle());
            b.setBullet(true);
            b.setLinearVelocity(d.mul(m_bulletVelocity).add(shooter.getBody().getLinearVelocity()));
            // TODO drain energy usage from game object if this is a beam weapon
            shooter.emitGameObject(bo); 
            m_blast=bo;
            m_flightTime=0;
        }else if(m_shooting && m_blast != null && m_flightTime > PRIME_COUNT){ 
            /* detonate m_blast */ 
            if(m_blast.getBody()!=null){
                System.out.println("kaboom");
                int n=8;
                float astep=(float)(2*Math.PI)/n;
                float a=m_blast.getBody().getAngle();
                for(int i=0;i<n;i++){
                    // TODO body might be null which means this bulllet collided with something
                    // need to figure out how to remove this reference when that happens
                        Body b=Game.game.createCircle(0.5f,0.2f);
                        BulletObject so=new BulletObject(b,5);
                        //System.out.println("body "+b+", m_blast "+m_blast+" with body "+m_blast.getBody());
                        b.setXForm(m_blast.getBody().getWorldCenter().add(Util.rotate(new Vec2(5,0),i*astep+a)),i*astep+a);
                        b.setLinearVelocity(Util.rotate(new Vec2(30,0),i*astep+a));
                        //m_blast.emitGameObject(so);
                        shooter.emitGameObject(so);
                }  
                Game.game.queueRemoveGameObject(m_blast);
                
            }else{
                //System.out.println("wall dud");
            }
            m_blast=null; 
            m_reloadTime=0;
        }else{
            if(m_blast != null){
                //System.out.println("waiting...");
            }
        }
    }

    public int getPortType(){ return 1; }

    public String getName(){ return "Tank Cannon"; }

    public float getVelocity(){ return m_bulletVelocity; }
}
