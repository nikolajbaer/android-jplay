package com.nikolajbaer.game;

/* java */
import java.lang.Math;

/* local */
import com.nikolajbaer.game.objects.*;

public class GamePlayer {
    private PlayerObject m_playerObject;

    public GamePlayer(PlayerObject go){
        m_playerObject=go;
    }

    public void tick(){
        double r=Math.random();
        if(r > 0.9){
            m_playerObject.forward();
        }else if(r < 0.1){
            m_playerObject.reverse();
        }else{
            m_playerObject.left();
        }
    }
}
