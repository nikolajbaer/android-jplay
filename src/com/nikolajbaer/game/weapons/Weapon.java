package com.nikolajbaer.game.weapons;

/* local */
import com.nikolajbaer.game.*;
import com.nikolajbaer.game.objects.*;

public abstract class Weapon {
    // pull the trigger down
    public abstract void triggerOn();
    
    // release the trigger
    public abstract void triggerOff();

    // the name
    public abstract String getName();

    // the type of weapon port this goes into..
    public abstract int getPortType(); 

    // the tick
    public abstract void tick(GameObject parent);

}
    
