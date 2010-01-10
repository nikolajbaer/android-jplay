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

}
