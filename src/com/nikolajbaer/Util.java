package com.nikolajbaer;

/* java */
import java.lang.Math;

/* jbox2d */
import org.jbox2d.common.Vec2;

public class Util {
    public static Vec2 rotate(Vec2 v,float a){
        return new Vec2((float)(v.x * Math.cos(a) - v.y * Math.sin(a)),
                       (float)(v.x * Math.sin(a) + v.y * Math.cos(a)));
    }

    /* angle from v1 to v2, provided center at 0,0 */
    public static float angleTo(Vec2 v1,Vec2 v2){
        double a1=Math.atan(v1.y/v1.x);
        double a2=Math.atan(v2.y/v2.x);
        return (float)(a2-a1);
    }

}
