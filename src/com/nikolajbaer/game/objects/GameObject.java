package com.nikolajbaer.game.objects;

/* java */
import java.lang.Math;
import java.util.ArrayList;

/* jbox2d */
import org.jbox2d.dynamics.Body; 
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

/* local */
import com.nikolajbaer.game.Game;
import com.nikolajbaer.Util;
import com.nikolajbaer.render.Renderable;
import com.nikolajbaer.render.RenderObject;

// CONSIDER maybe this should be an interface?
public abstract class GameObject implements Renderable {
    protected Body m_body;
    protected RenderObject m_renderObject;
    protected float thruster;

    protected ArrayList<GameObjectEventListener> m_gameObjectEventListeners;
    protected int m_damage=0; // CONSIDER do i really need this here?

    public GameObject(Body b){
        m_body=b;
        thruster=0.0f;

        m_gameObjectEventListeners = new ArrayList<GameObjectEventListener>();
    }

    public Vec2 getDir(){
        return Util.rotate(new Vec2(0,-1),m_body.getAngle());
    }

    /* tick
     *
     * do whatever per step. return false if this game object is to be removed.
     */
    public boolean tick(){ return true; }

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

    public float[] getWorldTransform(){
        Vec2 p=m_body.getPosition();
        float[] v={p.x,p.y,m_body.getAngle()};
        return v;
    }

    public Vec2 getPosition(){
        return m_body.getPosition();
    }

    // CONSIDER return string array. 
    // then when rendering we can send relative position/angle of each component (e.g. tank turret)
    public String getRenderKey(){ return "default"; }

    public RenderObject getRenderObject(){
        return m_renderObject;
    }

    public void setRenderObject(RenderObject ro){
        m_renderObject=ro;
    }

    public void clearRenderObject(){
         m_renderObject=null;
    }
    
}


