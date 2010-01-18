package com.nikolajbaer.game.objects;

/* java */
import java.lang.Math;

/* jbox2d */
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

/* AWT */
//import java.awt.geom.*;
//import java.awt.*;

/* local */
import com.nikolajbaer.game.Game;
import com.nikolajbaer.game.weapons.*;
import com.nikolajbaer.Util;

public class PlayerObject extends PolygonGameObject {
    //protected Color m_color;
    protected float m_hull; // TODO make it shields + hull
    protected float m_shields;
    protected float m_energy;
    protected static final float SHIELD_MAX = 100;
    protected static final float HULL_MAX = 100;
    protected static final float ENERGY_MAX = 100;
    protected static final float ENERGY_RECHARGE_RATE = 1;
    protected static final float SHIELD_RECHARGE_RATE = 0.1f;

    protected Weapon m_currentWeapon; // TODO make it have weapon ports
    protected static boolean m_isDead=false;
    
    public PlayerObject(Body b,float[] vertices){
        super(b,vertices);
        //m_color=c; 
        m_hull=HULL_MAX;
        m_shields=SHIELD_MAX;
        m_energy=ENERGY_MAX;
        m_currentWeapon=new TankCannon();
        //m_currentWeapon=new Blaster();
    }

    // TODO add alert system to warn player with issues in their tank
    // e.g. shields down, hull level critical, cannon reloading stalled

    public void left(){
        if(m_body==null){ return; }
        //m_body.setAngularVelocity(-1.0f);
        if(m_body.getAngularVelocity() > -MAX_ANG_VEL){
            m_body.applyTorque(-120.0f);
        }
    }
    
    public void right(){
        if(m_body==null){ return; }
        //m_body.setAngularVelocity(1.0f);
        if(m_body.getAngularVelocity() < MAX_ANG_VEL){
            m_body.applyTorque(120.0f);
        }
    }

    public void stopRotate(){
        if(m_body==null){ return; }
        m_body.setAngularVelocity(0.0f);
    }

    private void thrust(float m){
        if(m_body==null){ return; }
        if(m==0){ return; }
        Vec2 lv=m_body.getLinearVelocity();
        if(Math.abs(lv.length()) < MAX_LIN_VEL){
            Vec2 bodyVec = m_body.getWorldCenter();
            Vec2 dir=getDir();
            m_body.applyForce(dir.mul(m),bodyVec);
        }
    }

    public void halt(){
        //System.out.println("halting");
        //m_body.setLinearVelocity(new Vec2(0.0f,0.0f));
        thruster=0.0f;
    }

    public void forward(){
        Vec2 d=getDir().mul(8.0f);
        //System.out.println("changing linear velcity from "+m_body.getLinearVelocity()+" to "+d);
        //m_body.setLinearVelocity(d);
        thruster=25.0f;
    }

    public void reverse(){
        //Vec2 d=getDir().mul(-8.0f);
        //System.out.println("changing linear velcity from "+m_body.getLinearVelocity()+" to "+d);
        //m_body.setLinearVelocity(d);
        thruster=-12.0f;
    }

    /*
    public void draw( Graphics2D g ){
        Stroke orig_s=g.getStroke();
        g.setStroke(new BasicStroke(2.0f)); 
        g.setColor(m_color);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        g.rotate(m_body.getAngle());

        g.drawPolygon(x_pts,y_pts,x_pts.length);
        g.setStroke(orig_s);
    }
    */

    public boolean tick(){
        // recharge energy and shields
        m_energy+=ENERGY_RECHARGE_RATE;
        if(m_energy > ENERGY_MAX){ m_energy=ENERGY_MAX; }
        // CONSIDER should shields make energy recharge slower?        
        m_shields += SHIELD_RECHARGE_RATE;
        if(m_shields > SHIELD_MAX){ m_shields = SHIELD_MAX; } 

        m_currentWeapon.tick(this);
        thrust(thruster);
        return true;
    }

    public void triggerOn(){
        m_currentWeapon.triggerOn();
    }

    public void triggerOff(){
        m_currentWeapon.triggerOff();
    } 

    // CONSIDER make beam weapons apply more damage to shields, and physical apply less,
    // with vice versa on the hull
    public boolean applyDamage(int d){
        m_shields -= d;
        if(m_shields < 0){
            //System.out.println("applying damage to hull!");
            float d2=-1*m_shields;
            m_hull -= d2;
            if(m_hull < 0){
                return false;
            }
        }
        return true;
    } 

    public void doDestroy(){
        int n=6;
        float astep=(float)(2*Math.PI)/n;
        for(int i=0;i<n;i++){
            Body b=Game.game.createCircle(0.5f,0.2f);
            ShrapnelObject so=new ShrapnelObject(b,4);
            b.setXForm(m_body.getWorldCenter().add(Util.rotate(new Vec2(2,0),i*astep)),0);
            b.setLinearVelocity(Util.rotate(new Vec2(10,0),i*astep));
            emitGameObject(so);
        }  
        m_isDead=true; 
    }

    public float getEnergyRatio(){ return (float)m_energy/ENERGY_MAX; } 

    public float getHullRatio(){ return (float)m_hull/HULL_MAX; }

    public float getShieldsRatio(){ return (float)m_shields/SHIELD_MAX; }

    public float getEnergy(){ return m_energy; } 

    public float getHull(){ return m_hull; }

    public float getShields(){ return m_shields; }

    /* Draws energy from ship. Return the amount drawn (may be less than requested) */
    public float drawEnergy(int e){
        float l=m_energy-e;
        if(l<0){ 
            m_energy=0;
            return e+l;
        }else{
            m_energy=l;
        }
        return l;
    }

    // TODO need to figure out efficiently is a player is still in the game in the AI
    // perhapsthey should target a player rather than a gameobject?
    public boolean isAlive(){
        return !m_isDead;
    }

    public float getWeaponVelocity(){
        return m_currentWeapon.getVelocity(); 
    }

    /*
    public Color getColor(){
        return m_color;
    }
    */

    public String getRenderKey(){ return "tank"; }
}

