import org.jbox2d.dynamics.World;
import org.jbox2d.common.Vec2;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.collision.ShapeDef;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import java.awt.*;

public class Game {
	private World m_world;
    private Body m_body;

    public Game(int width,int height,float pixelsPerMeter){
        // make world 2 times wider and higher
		Vec2 minWorldAABB = new Vec2(-2*width*.5f/pixelsPerMeter,-2*height*.5f/pixelsPerMeter);
		Vec2 maxWorldAABB = minWorldAABB.mul(-1.0f);
		m_world = new World(new AABB(minWorldAABB,maxWorldAABB),new Vec2(0,-9),true);
  
        // create circle def
  		CircleDef cd = new CircleDef();
		cd.radius = 10;
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
		b.setXForm(new Vec2(0,0), 0.0f);
    }

    private Vec2 toScreen(Vec2 v){
        return v;
    }

    private Vec2 toWorld(Vec2 v){
        return v;
    }

    public void tick(){
        m_world.step(1.0f/60.0f,1);
    }

    public void draw( Graphics2D g ){
        g.setColor(Color.black);
        g.fillOval(100,100, 10, 10);
    }
}
