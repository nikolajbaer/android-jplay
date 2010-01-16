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

/* AWT Specific */
//import java.awt.*;
//import java.awt.geom.*;

/* local */
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.Util;
import com.nikolajbaer.render.Renderable;

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
    private ArrayList<GameObject> m_gameObstacles;
    private PlayerObject m_player;
    private boolean[][] m_obstacleGrid;

    //public static final float PPM = 10.0f;

    // CONSIDER currently the simplest way to manage physics world interrelationships (see PlayerObect GO create)
    public static Game game=null; // singleton

    // HACK keep canvas width/height to draw HUD
    // TODO detach display of game from game itself
    //private int m_canvasWidth;
    //private int m_canvasHeight;

    public Game(int width,int height){
        // set world width/height
        //m_width=1/PPM * width;
        //m_height=1/PPM * height;
        m_width=width;
        m_height=height;
        //m_canvasWidth=width;
        //m_canvasHeight=height;

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

        m_gameObjects=new ArrayList<GameObject>();
        m_toRemove=new ArrayList<GameObject>();
        m_gamePlayers=new ArrayList<GamePlayer>();
        m_gameObstacles=new ArrayList<GameObject>();
        m_renderables=new ArrayList<Renderable>();

        // for now generate game tanks
        float[] verts={-1.0f,1.0f, 0.0f,-2.0f,1.0f,1.0f};

        Vec2 mid=new Vec2(m_width/2.0f,m_height/2.0f);
        Vec2 offset=new Vec2(m_width/2*0.75f,0);
        int np=2; //6;
        for(int i=0;i<np;i++){
            //Body b=createRect(0.5f,-1.0f,-1.0f,2.0f,2.0f);
            Body b=createPolygon(1.0f, verts );

            double a=(2*Math.PI)/np;
            Vec2 rv=Util.rotate( offset, (float)(i*a) );
            float ra=(float)(i*a < Math.PI ? i*a+Math.PI : i*a-Math.PI);
            b.setXForm( mid.add(rv) ,(float)(ra+Math.PI/2)); // i guess it goes from 0,1 not 1,0
            PlayerObject po=new PlayerObject(b,verts);
            po.addGameObjectEventListener(this);
            addGameObject(po);
            if(i==0){ 
                m_player=po; 
                LivePlayer lp=new LivePlayer(m_player);
                m_gamePlayers.add(lp);
            }else{
                HunterPlayer gp=new HunterPlayer(po);
                m_gamePlayers.add(gp);
            }
        }

        // TODO add obstacle grid
        // and randomly choose a couple squares to fill in
        // and create polygon rects on those   
        // TODO add A* path finding to AI
        /*
        int OBS_GRIDWIDTH=20;
        int OBS_GRIDHEIGHT=40;
        float sqw=(m_width/(float)OBS_GRIDWIDTH);
        float sqh=(m_height/(float)OBS_GRIDHEIGHT);
        m_obstacleGrid=new boolean[OBS_GRIDWIDTH][OBS_GRIDHEIGHT];
        for(int i=0;i<OBS_GRIDWIDTH;i++){
            for(int j=0;j<OBS_GRIDHEIGHT;j++){
                // TODO more efficient to not do rand every iteration..
                if(Math.random()>0.98){
                    m_obstacleGrid[i][j]=true;
                    float x=i*sqw;
                    float y=j*sqh;
                    float w=sqw;
                    float h=sqh;
                    float[] overts={x,y, x,y+h, x+w,y+h, x+w,y };
                    Body b=createRect(0.0f,x,y,w,h);
                    PolygonGameObject pog=new PolygonGameObject(b,overts);
                    m_gameObstacles.add(pog);
                    //m_gameObjects.add(pog);
                    addGameObject(pog);
                }else{
                    m_obstacleGrid[i][j]=false;
                }
            }
        }
        */
        /*
        int nobst=5;
        for(int i=0;i<nobst;i++){
            float x=0.0f;
            float y=i*3.0f;
            float w=5.0f;
            float h=1.0f;
            float[] overts={x,y, x,y+h, x+w,y+h, x+w,y };
            Body b=createRect(0.0f,x,y,w,h);
            PolygonGameObject pog=new PolygonGameObject(b,overts);
            m_gameObstacles.add(pog);
            m_gameObjects.add(pog);
        }
        */
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
        bd.linearDamping=0.98f;
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

    /*
    public void draw( Graphics2D g ){
        // draw game objects 
        AffineTransform t=g.getTransform();
        for(int i=0;i<m_gameObjects.size();i++){
            m_gameObjects.get(i).draw(g);
            g.setTransform(new AffineTransform(t));
        }
    
        // draw HUD 
        // TODO make a detached rendering engine. 
        // HACK hardcoding width here for the moment to render hud
        int np=m_gamePlayers.size();
        int msz=(int)(m_canvasWidth/np-10);
        for(int i=0;i<np; i++){
            // CONSIDER assumes player object
            PlayerObject p=(PlayerObject)m_gamePlayers.get(i).getGameObject();
            //g.setColor(p.getColor());
            int bx=i*(msz+10)+5;
            // energy 
            g.drawRect(bx,m_canvasHeight-35,msz,5);
            g.fillRect(bx,m_canvasHeight-35,(int)(msz*p.getEnergyRatio()),5);
            // shields
            g.drawRect(bx,m_canvasHeight-28,msz,5);
            g.fillRect(bx,m_canvasHeight-28,(int)(msz*p.getShieldsRatio()),5);
            // hull 
            g.drawRect(bx,m_canvasHeight-21,msz,5);
            g.fillRect(bx,m_canvasHeight-21,(int)(msz*p.getHullRatio()),5);

        }
    }
    */

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
