import java.awt.*;
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

public class ShrapnelObject extends PolygonGameObject {
    private Color m_color;
    private int impacts;

    public ShrapnelObject(Body b,Color c){
        super(b,new float[]{-1,-1,0,1,1,-1});
        m_color=c;
        impacts=0;
    }

    public boolean survivesImpact(){
        impacts++;
        return impacts < 1;
    }

    public boolean doesDamage(){
        return false;
    }

    public void draw( Graphics2D g ){
        g.setColor(m_color);
        Vec2 p=Game.toScreen(m_body.getPosition());
        g.translate(p.x,p.y);
        g.fillOval(0,0,2,2);
    }


}
