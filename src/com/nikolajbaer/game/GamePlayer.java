package com.nikolajbaer.game;

/* java */
import java.lang.Math;

/* local */
import com.nikolajbaer.game.objects.*;

public abstract class GamePlayer {
    protected PlayerObject m_playerObject;

    // TODO make sure to stop the tick when game player is destroyed
    public GamePlayer(PlayerObject go){
        m_playerObject=go;
    }

    public abstract void tick();

    public GameObject getGameObject(){
        return m_playerObject;
    }
}
