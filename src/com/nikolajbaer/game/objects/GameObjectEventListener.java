package com.nikolajbaer.game.objects;

/* java */
import java.util.EventListener;

public interface GameObjectEventListener extends EventListener{
    public void gameObjectCreated(GameObjectEvent e);
    public void gameObjectDestroyed(GameObjectEvent e);   
}
