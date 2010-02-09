package com.nikolajbaer.game.players;


/* java */
import java.lang.Math;
import java.util.ArrayList;

/* jbox2d */
import org.jbox2d.common.Vec2;

/* local */
import com.nikolajbaer.game.objects.*;
import com.nikolajbaer.game.Game;
import com.nikolajbaer.Util;

public class LambPlayer extends HunterPlayer {

    public LambPlayer(PlayerObject go){
        super(go);
    }

    protected void doAttack(){
        // lambs only love
    }
}
