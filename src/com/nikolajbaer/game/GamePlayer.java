package com.nikolajbaer.game;

/* java */
import java.lang.Math;

/* local */
import com.nikolajbaer.game.objects.*;

public abstract class GamePlayer {
    protected PlayerObject m_playerObject;

    public GamePlayer(PlayerObject go){
        m_playerObject=go;
    }

    public abstract void tick();

    public GameObject getGameObject(){
        return m_playerObject;
    }
}
