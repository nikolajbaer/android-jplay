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
    private static float m_bulletVelocity = 12.0f;
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
        if(m_shooting && m_blast == null ){
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
        }else if(!m_shooting && m_blast != null){ 
            /* detonate m_blast */ 
            int n=8;
            float astep=(float)(2*Math.PI)/n;
            for(int i=0;i<n;i++){
                Body b=Game.game.createCircle(0.5f,0.2f);
                // TODO body might be null which means this bulllet collieded with something
                // need to figure out how to remove this reference when that happens
                if(m_blast.getBody()!=null){
                    BulletObject so=new BulletObject(b,5);
                    System.out.println("body "+b+", m_blast "+m_blast+" with body "+m_blast.getBody());
                    b.setXForm(m_blast.getBody().getWorldCenter().add(Util.rotate(new Vec2(2,0),i*astep)),0);
                    b.setLinearVelocity(Util.rotate(new Vec2(10,0),i*astep));
                    m_blast.emitGameObject(so);
                }
            }  
            // TODO destroy m_blast
            m_blast=null; 
        }
    }

    public int getPortType(){ return 1; }

    public String getName(){ return "Tank Cannon"; }

    public float getVelocity(){ return m_bulletVelocity; }
}
