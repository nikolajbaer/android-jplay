import java.awt.*;
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;
import java.awt.geom.*;
import java.lang.Math;

public class PlayerObject extends PolygonGameObject {
    private Color m_color;
    private int health;
    
    public PlayerObject(Body b,float[] vertices,Color c){
        super(b,vertices);
        m_color=c; 
        health=100;

    }

    public void left(){
        //m_body.setAngularVelocity(-1.0f);
        if(m_body.getAngularVelocity() > -MAX_ANG_VEL){
            m_body.applyTorque(-120.0f);
        }
    }
    
    public void right(){
        //m_body.setAngularVelocity(1.0f);
        if(m_body.getAngularVelocity() < MAX_ANG_VEL){
            m_body.applyTorque(120.0f);
        }
    }

    public void stopRotate(){
        m_body.setAngularVelocity(0.0f);
    }

    public void thrust(float m){
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

    public void draw( Graphics2D g ){
        Stroke orig_s=g.getStroke();
        g.setStroke(new BasicStroke(2.0f)); 
        g.setColor(m_color);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        g.rotate(m_body.getAngle());

        //int mp=(int)(pixelsPerMeter);
        //g.fillRect(-mp,-mp,mp*2,mp*2);
        g.drawPolygon(x_pts,y_pts,x_pts.length);
        g.setStroke(orig_s);
    }

    public boolean tick(){
        thrust(thruster);
        return true;
    }

    public void triggerOn(){
        // TODO should be able to just tell the Game object to
        // create a BulletObject going in V direction, not create a body as well
        // TODO while trigger is on manage firing in tick with a reloadRate
        Vec2 d=getDir();
        Body b=Game.game.createCircle(5.0f,0.5f);
        BulletObject bo=new BulletObject(b,50);
        b.setXForm(m_body.getWorldCenter().add(d.mul(3)),m_body.getAngle());
        b.setBullet(true);
        b.setLinearVelocity(d.mul(10));
        emitGameObject(bo); 
    }

    public void triggerOff(){
    } 

    public boolean applyDamage(int d){
        health-=d;
        return health>0;
    } 

    public void doDestroy(){
        int n=6;
        float astep=(float)(2*Math.PI)/n;
        for(int i=0;i<n;i++){
            Body b=Game.game.createCircle(0.5f,0.2f);
            ShrapnelObject so=new ShrapnelObject(b,m_color);
            b.setXForm(m_body.getWorldCenter().add(rotate(new Vec2(2,0),i*astep)),0);
            b.setLinearVelocity(rotate(new Vec2(10,0),i*astep));
            emitGameObject(so);
        }  
       
    }
}

