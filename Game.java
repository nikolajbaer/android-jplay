import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.collision.ShapeDef;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import java.awt.*;
import java.awt.geom.*;

public class Game {
	private World m_world;
    private float m_width;
    private float m_height;
    private Body[] m_boundingBox;
    private float m_pixelsPerMeter;
    private Body[] tmpBodies;

    public Game(int width,int height,float pixelsPerMeter){
        // set world width/height
        m_width=1/pixelsPerMeter * width;
        m_height=1/pixelsPerMeter * height;
        m_pixelsPerMeter=pixelsPerMeter;

        // make world 2 times wider and higher
		Vec2 minWorldAABB = new Vec2(-m_width,-m_height);
		Vec2 maxWorldAABB = new Vec2(m_width*2,m_height*2);
        System.out.println("World Bounds: "+minWorldAABB+" -- "+maxWorldAABB);
		m_world = new World(new AABB(minWorldAABB,maxWorldAABB),new Vec2(0,9),true);
 
        // create screen bounding box
        m_boundingBox=new Body[4]; 
        m_boundingBox[0]=createStaticRect( -10.0f,0.0f, 10.0f,m_height); //left
        m_boundingBox[1]=createStaticRect( 0.0f,-10.0f, m_width, 10.0f); //top
        m_boundingBox[2]=createStaticRect( m_height,0.0f,10.0f,m_height); //right
        m_boundingBox[3]=createStaticRect( 0.0f,m_height, m_width, 10.0f); //bottom

        // create circle def
        /*
  		CircleDef cd = new CircleDef();
		cd.radius = 1;
        cd.density=1.0f;
        cd.friction=0.4f;
        cd.restitution=0.1f;
        cd.isSensor=false;

        // create body def
		BodyDef bd = new BodyDef();
        // create body
		Body b = m_world.createBody(bd);
		b.createShape(cd);
        b.setMassFromShapes();
		b.setXForm(new Vec2(10,10), 0.0f);
        tmpBody=b;
        */
        tmpBodies=new Body[5];
        for(int i=0;i<tmpBodies.length;i++){
            Body b=createRect(0.5f,-1.0f,-1.0f,2.0f,2.0f);
            b.setXForm(new Vec2(i*2+2,10.0f),i);
            tmpBodies[i]=b;
        }
    }

    private Body createRect(float density,float x0,float y0, float width, float height){
        return createPolygon(density,x0,y0,x0+width,y0, x0+width,y0+height, x0,y0+height);
    }

    private Body createStaticRect(float x0,float y0, float width, float height){
        Body b = createPolygon(0.0f,0,0, width,0, width,height, 0,height);
        b.setXForm(new Vec2(x0,y0),0.0f);
        return b;
    }

    private Body createPolygon(float density,float... vertices){
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
        Body b=m_world.createBody(bd);
        b.createShape(pd);
        b.setMassFromShapes();
        return b;
    }

    private Vec2 toScreen(Vec2 v){
        return new Vec2(v.x*m_pixelsPerMeter,v.y*m_pixelsPerMeter);
    }

    private Vec2 toWorld(Vec2 v){
        return new Vec2(v.x*(1.0f/m_pixelsPerMeter),v.y*(1.0f/m_pixelsPerMeter));
    }

    public void tick(){
        m_world.step(1.0f/60.0f,1);
    }

    public void draw( Graphics2D g ){
        AffineTransform t=g.getTransform();
        g.setColor(Color.black);
        //System.out.println("v: "+p);
        for(int i=0;i<tmpBodies.length;i++){
            Vec2 p=toScreen(tmpBodies[i].getPosition());
            g.translate(p.x,p.y);
            g.rotate(tmpBodies[i].getAngle());
            int mp=(int)(m_pixelsPerMeter);
            g.fillRect(-mp,-mp,mp*2,mp*2);
            g.setTransform(new AffineTransform(t));
        }
    }
}
