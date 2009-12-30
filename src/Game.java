import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.collision.ShapeDef;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

// CONSIDER phsyics should be refactored into its own engine
public class Game implements GameObjectEventListener,ContactListener {
	private World m_world;
    private float m_width;
    private float m_height;
    private Body[] m_boundingBox;
    private ArrayList<GameObject> m_gameObjects;
    private ArrayList<GameObject> m_toRemove;
    private PlayerObject m_player;

    public static final float PPM = 10.0f;

    // CONSIDER currently the simplest way to manage physics world interrelationships (see PlayerObect GO create)
    public static Game game=null; // singleton

    public Game(int width,int height){
        // set world width/height
        m_width=1/PPM * width;
        m_height=1/PPM * height;

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


        // for now generate game tanks
        float[] verts={-1.0f,1.0f, 0.0f,-2.0f,1.0f,1.0f};
        for(int i=0;i<5;i++){
            //Body b=createRect(0.5f,-1.0f,-1.0f,2.0f,2.0f);
            Body b=createPolygon(1.0f, verts );

            b.setXForm(new Vec2(i*4+2,10.0f),i);
            PlayerObject po=new PlayerObject(b,verts,(i==0)?Color.red:Color.blue);
            po.addGameObjectEventListener(this);
            po.getBody().setUserData(po); // TODO make this less hackish 
            m_gameObjects.add(po);
            if(i==0){ m_player=po; }
        }
    
        /*
        float[] verts2={1.0f,1.0f,0.0f,2.0f,-1.0f,-2.0f,3.0f,-2.0f,3.0f,0.0f};
        for(int i=0;i<3;i++){
            //Body b=createRect(0.5f,-1.0f,-1.0f,2.0f,2.0f);
            Body b=createPolygon(1.0f, verts2 );

            b.setXForm(new Vec2(i*4+2,20.0f),i);
            m_gameObjects.add(new GameObject(b,verts2));
        }
        */
        game=this;
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

    public static Vec2 toScreen(Vec2 v){
        return new Vec2(v.x*PPM,v.y*PPM);
    }

    // TODO refactor this?
    public PlayerObject getPlayer(){
        return m_player;
    }

    public static Vec2 toWorld(Vec2 v){
        return new Vec2(v.x*(1.0f/PPM),v.y*(1.0f/PPM));
    }

    public void tick(){
        // TODO use iterators
        // TODO use queue for toRemove
        for(int i=0;i<m_toRemove.size();i++){
            GameObject goner=m_toRemove.get(i);
            removeGameObject(goner);
            m_toRemove.remove(goner);
        }
        for(int i=0;i<m_gameObjects.size();i++){
            m_gameObjects.get(i).tick();
        }
        m_world.step(1.0f/60.0f,1);
    }

    public void draw( Graphics2D g ){
        AffineTransform t=g.getTransform();
        for(int i=0;i<m_gameObjects.size();i++){
            m_gameObjects.get(i).draw(g);
            g.setTransform(new AffineTransform(t));
        }
    }

    public void gameObjectCreated(GameObjectEvent e){
        GameObject c=e.getCreated();
        m_gameObjects.add(c);
    }

    public void gameObjectDestroyed(GameObjectEvent e){ 
        // TODO remove target
        removeGameObject(e.getTarget());
        // TODO remove taget body from m_world
    }

    private void queueRemoveGameObject(GameObject go){
        m_toRemove.add(go);
    }

    private void removeGameObject(GameObject go){
        Body b=go.getBody();
        b.setUserData(null);
        m_world.destroyBody(b);
        m_gameObjects.remove(go);
   }

    // Contact Listener manages bullet damage
    // TODO remove this from Game to seperate points manager
    // see integrations/slick/SlickTestMain.java 
    public void add(ContactPoint p){
        Body b1=p.shape1.getBody();
        Body b2=p.shape2.getBody();
        //System.out.println("contact made" + b1 +" - +"+b2);

        GameObject g1=(GameObject)b1.getUserData();
        GameObject g2=(GameObject)b2.getUserData();

        /* apply damage and remove objects if needed */       
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
            g1.doDestroy();
        }
        if(removeg2){
            queueRemoveGameObject(g2);
            g2.doDestroy();
        }
    }

    public void persist(ContactPoint p){
    }
    public void remove(ContactPoint p){
    } 
    public void result(ContactResult p){
    }
}
