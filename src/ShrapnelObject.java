import java.awt.*;
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

public class ShrapnelObject extends PolygonGameObject {
    private Color color;

    public ShrapnelObject(Body b,Color c){
        super(b,new float[]{-1,-1,0,1,1,-1});
        color=c;
    }

    public boolean survivesImpact(){
        return false;
    }

    public boolean doesDamage(){
        return false;
    }
}
