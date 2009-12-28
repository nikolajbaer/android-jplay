import java.awt.*;
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

public class BulletObject extends GameObject {

    public BulletObject(Body b,int damage){
        super(b);
        this.m_damage=damage;
    }

    public void tick(){ }

    public void draw( Graphics2D g ){
        // TODO: figure out why this isn't drawing in the right position!?!?!
        g.setColor(Color.black);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        //g.rotate(m_body.getAngle());
        //Vec2 d=Game.toScreen(getDir());
        //int mp=(int)(pixelsPerMeter);
        //g.fillRect(-mp,-mp,mp*2,mp*2);
        int w=(int)(0.5*Game.PPM);
        g.fillOval((int)p.x,(int)p.y,w,w);
 
    }

}
