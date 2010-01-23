package com.nikolajbaer.game.objects;

/* jbox2d */
import org.jbox2d.dynamics.Body; import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.common.Vec2;

/* local */
import com.nikolajbaer.game.Game;

// CONSIDER awkward, could be refactored
public class PolygonGameObject extends GameObject {
    protected float[] m_vertices;
    //protected int[] x_pts; protected int[] y_pts;

    // CONSIDER obsolete as drawing is abstracted
    public PolygonGameObject(Body b,float[] vertices){
        super(b);
        m_vertices=vertices;
    }

    public String getRenderKey(){ return "filled_polygon"; }
}
