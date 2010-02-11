package com.nikolajbaer.game;

/* java */
import java.util.ArrayList;

/* jbox2d */
import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.collision.ShapeDef;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;

/* local */
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.Util;
import com.nikolajbaer.render.Renderable;
import com.nikolajbaer.game.players.*;

// CONSIDER phsyics should be refactored into its own engine
public class Game implements GameObjectEventListener,ContactListener {
	private World m_world;
    private float m_width;
    private float m_height;
    private Body[] m_boundingBox;
    // CONSIDER should i be keeping the same object in multiple lists?
    private ArrayList<GameObject> m_gameObjects;
    private ArrayList<Renderable> m_renderables;
    private ArrayList<GameObject> m_toRemove;
    private ArrayList<GamePlayer> m_gamePlayers;
    private ArrayList<ObstacleObject> m_gameObstacles;
    private PlayerObject m_player;
    private boolean[][] m_obstacleGrid;
    private final static int OBSTACLE_GRID_SIZE=1;

    // CONSIDER currently the simplest way to manage physics world interrelationships (see PlayerObect GO create)
    public static Game game=null; // singleton

    public Game(int width,int height){
        // set world width/height
        m_width=width;
        m_height=height;
        // make world 2 times wider and higher
		Vec2 minWorldAABB = new Vec2(-m_width,-m_height);
		Vec2 maxWorldAABB = new Vec2(m_width*2,m_height*2);
        System.out.println("World Bounds: "+minWorldAABB+" -- "+maxWorldAABB);
		m_world = new World(new AABB(minWorldAABB,maxWorldAABB),new Vec2(0,0),true);

        // TODO pull out contact listener to its own class
        m_world.setContactListener(this);
        // create screen bounding box
        m_boundingBox=new Body[4]; 
        m_boundingBox[0]=createStaticRect( -10.0f,0.0f, 10.0f,m_height); //left
        m_boundingBox[1]=createStaticRect( 0.0f,-10.0f, m_width, 10.0f); //top
        m_boundingBox[2]=createStaticRect( m_width,0.0f,10.0f,m_height); //right
        m_boundingBox[3]=createStaticRect( 0.0f,m_height, m_width, 10.0f); //bottom

        // Create arrays for various objects in game
        m_gameObjects=new ArrayList<GameObject>();
        m_toRemove=new ArrayList<GameObject>();
        m_gamePlayers=new ArrayList<GamePlayer>();
        m_gameObstacles=new ArrayList<ObstacleObject>();
        m_renderables=new ArrayList<Renderable>();

        // grid of obstacles (dfeault to false)
        // for future A* path finding
        m_obstacleGrid=new boolean[(int)(m_width/OBSTACLE_GRID_SIZE)][(int)(m_height/OBSTACLE_GRID_SIZE)];
    }
    
    public void addPlayer(GamePlayer gp){
        addPlayer(gp,false);
    }

    public void addPlayer(GamePlayer gp,boolean is_player){
        addGameObject(gp.getGameObject());
        gp.getGameObject().addGameObjectEventListener(this);
        m_gamePlayers.add(gp);
        if(is_player){
            m_player=(PlayerObject)gp.getGameObject();
        }
    }

    public void addObstacle(int i,int j){
        Body b=createStaticRect(i*OBSTACLE_GRID_SIZE,j*OBSTACLE_GRID_SIZE,OBSTACLE_GRID_SIZE,OBSTACLE_GRID_SIZE);
        ObstacleObject o=new ObstacleObject(b);
        m_gameObstacles.add(o);
        m_obstacleGrid[i][j]=true;
        addGameObject(o);
    }

    public Body createCircle(float density, float radius){
        // create circle def
        CircleDef cd = new CircleDef();
        cd.radius = radius;
        cd.density=density;
        cd.friction=0.4f;
        cd.restitution=0.1f;
        cd.isSensor=false;

        BodyDef bd = new BodyDef();
        Body b = m_world.createBody(bd);
        b.createShape(cd);
        b.setMassFromShapes();
        return b;
    }

    public Body createRect(float density,float x0,float y0, float width, float height){
        float[] verts={x0,y0,x0+width,y0, x0+width,y0+height, x0,y0+height};
        return createPolygon(density,verts);
    }

    public Body createStaticRect(float x0,float y0, float width, float height){
        float[] verts={0,0, width,0, width,height, 0,height};
        Body b = createPolygon(0.0f,verts);
        b.setXForm(new Vec2(x0,y0),0.0f);
        return b;
    }

    public Body createPolygon(float density,float[] vertices){
        if(vertices.length % 2 != 0){
            throw new IllegalArgumentException("Vertices must be given as pairs of x,y coordinates, " +
											   "but number of passed parameters was odd.");
        }

        // create the shape and add the vertices
        PolygonDef pd = new PolygonDef();
        for(int i=0;i<vertices.length/2; i++){
            Vec2 v=new Vec2(vertices[2*i],vertices[2*i+1]);
            pd.addVertex(v);
        } 
        pd.density=density;
        pd.friction=0.4f;
        pd.restitution=0.1f;

        // create the body def and body
        BodyDef bd = new BodyDef();
        bd.isBullet=false;
        bd.linearDamping=0.5f;
        bd.angularDamping=0.9f;
        Body b=m_world.createBody(bd);
        b.createShape(pd);
        b.setMassFromShapes();
        return b;
    }

    /*
    public static Vec2 toScreen(Vec2 v){
        return new Vec2(v.x*PPM,v.y*PPM);
    }
    */

    // TODO refactor this?
    // CONSIDER should be LivePlayer not player object?
    public PlayerObject getPlayer(){
        return m_player;
    }

    /*
    public static Vec2 toWorld(Vec2 v){
        return new Vec2(v.x*(1.0f/PPM),v.y*(1.0f/PPM));
    }
    */

    public void tick(){
        // TODO use iterators
        // TODO use queue for toRemove
        for(int i=0;i<m_toRemove.size();i++){
            GameObject goner=m_toRemove.get(i);
            removeGameObject(goner);
            m_toRemove.remove(goner);
        }
        for(int i=0;i<m_gameObjects.size();i++){
            GameObject o=m_gameObjects.get(i);
            if(!o.tick()){
                removeGameObject(o);
            }
        }
        for(int i=0;i<m_gamePlayers.size();i++){
            m_gamePlayers.get(i).tick();
        }
        m_world.step(1.0f/40.0f,1);
    }

    public void gameObjectCreated(GameObjectEvent e){
        addGameObject(e.getCreated());
    }

    public void addGameObject(GameObject go){
        go.getBody().setUserData(go); // HACK
        m_gameObjects.add(go);
        m_renderables.add(go);
    }

    public void gameObjectDestroyed(GameObjectEvent e){ 
        removeGameObject(e.getTarget());
    }

    /* 
        bodies can not be removed in a contact callback, so we must queue them 
        queue is processed before tick
    */
    private void queueRemoveGameObject(GameObject go){
        m_toRemove.add(go);
        //System.out.println("queueing remove of "+go);
    }

    private void removeGameObject(GameObject go){
        Body b=go.getBody();
        if(b==null){ 
            System.out.println("Woops, trying to remove an object that has a null body: "+go);
            return; 
        }
        //System.out.println("removing "+go+" with body "+b);
        b.setUserData(null);
        // make sure to set userdata null before do destroy
        go.doDestroy();
        go.removeBody();
        m_world.destroyBody(b);
        m_gameObjects.remove(go);
        m_renderables.remove(go);
        //remove from other lists?
    }

    public ArrayList<GamePlayer> getPlayers(){
        return m_gamePlayers;
    }
    public ArrayList<GamePlayer> players;

    // Contact Listener manages bullet damage
    // TODO remove this from Game to seperate points manager
    // see integrations/slick/SlickTestMain.java 
    public void add(ContactPoint p){
        Body b1=p.shape1.getBody();
        Body b2=p.shape2.getBody();
        //System.out.println("contact made" + b1 +" - +"+b2);

        GameObject g1=(GameObject)b1.getUserData();
        GameObject g2=(GameObject)b2.getUserData();

        // apply damage and remove objects if needed
        boolean removeg1=false;
        boolean removeg2=false; 
        if(g1 != null && g2 != null){
            if(g1.doesDamage()){
                removeg2=!g2.applyDamage(g1.getDamage());
            }
            if(g2.doesDamage()){
                removeg1=!g1.applyDamage(g2.getDamage());
            }
        }
        if(g1 != null && !g1.survivesImpact()){ removeg1=true; }
        if(g2 != null && !g2.survivesImpact()){ removeg2=true; }

        // we have to queue a remove because this is locked during callbacks
        // CONSIDER do i destroy a game object here or in the remove. 
        // If it is here that allows me to remove a game object in the future without a fancy destroy
        if(removeg1){
            queueRemoveGameObject(g1);
        }
        if(removeg2){
            queueRemoveGameObject(g2);
        }
    }

    public void persist(ContactPoint p){
    }
    public void remove(ContactPoint p){
    } 
    public void result(ContactResult p){
    }

    public ArrayList<Renderable> getRenderables(){
        return m_renderables;
    }
}
