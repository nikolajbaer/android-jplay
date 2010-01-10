package com.nikolajbaer.game.objects;

/* java */
import java.lang.Math;
import java.util.ArrayList;

/* jbox2d */
import org.jbox2d.dynamics.Body; 
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

/* AWT */
import java.awt.*;
import java.awt.geom.*;

/* local */
import com.nikolajbaer.game.Game;
import com.nikolajbaer.Util;

// CONSIDER maybe this should be an interface?
public abstract class GameObject {
    protected Body m_body;
    protected float thruster;
    protected static float MAX_LIN_VEL=10.0f;
    protected static float MAX_ANG_VEL=1.0f;

    protected ArrayList<GameObjectEventListener> m_gameObjectEventListeners;
    protected int m_damage=0; // CONSIDER do i really need this here?

    public GameObject(Body b){
        m_body=b;
        thruster=0.0f;

        m_gameObjectEventListeners = new ArrayList<GameObjectEventListener>();
    }

    // TODO make this more accessible
    // CONSDIER stupid that this isn't in Vec2

    public Vec2 getDir(){
        return Util.rotate(new Vec2(0,-1),m_body.getAngle());
    }

    /* tick
     *
     * do whatever per step. return false if this game object is to be removed.
     */
    public boolean tick(){ return true; }

    // TODO this should be abstracted into a GameObjectDisplay class
    // so i can make game code separate from display code to port to Android
    public abstract void draw( Graphics2D g );
    // do nothing

    // TODO shouldn't this kind of framework already be pre-packaged?
    public void addGameObjectEventListener(GameObjectEventListener l){
        m_gameObjectEventListeners.add(l);
    }

    public void removeGameObjectEventListener(GameObjectEventListener l){
        m_gameObjectEventListeners.remove(l);
    }

    protected void dispatchGameObjectCreatedEvent(GameObjectEvent e){
        for(int i=0;i<m_gameObjectEventListeners.size(); i++){
            m_gameObjectEventListeners.get(i).gameObjectCreated(e);
        }
    }  

    protected void dispatchGameObjectDestroyedEvent(GameObjectEvent e){
        for(int i=0;i<m_gameObjectEventListeners.size(); i++){
            m_gameObjectEventListeners.get(i).gameObjectDestroyed(e);
        }
    }  

    public Body getBody(){
        return m_body;
    }

    public boolean doesDamage(){
        return m_damage > 0;
    }

    public int getDamage(){
        return m_damage;
    }

    /* return true if it survives the damage done */
    public boolean applyDamage(int d){
        return true;
    }

    public boolean survivesImpact(){
        return true;
    }

    /* triggered when an object is being destroyed (e.g. make shrapnel or something) */
    public void doDestroy(){
    }

    // HACK made this public so the weapon can use the game object's emit..
    // CONSIDER should the weapon be allowed to emit its own objects? seems like something it would do
    public void emitGameObject(GameObject go){
        dispatchGameObjectCreatedEvent(new GameObjectEvent(this,go)); 
    }

    public void removeBody(){
        //System.out.println("I am removing body" + this);
        m_body=null;
    }
    
}


