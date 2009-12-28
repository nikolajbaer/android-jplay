import java.util.EventObject;

public class GameObjectEvent extends EventObject {
    private GameObject created;

    public GameObjectEvent(GameObject target){
        super(target);
        created=null;
    }

    public GameObjectEvent(GameObject target,GameObject created){
        super(target);
        this.created=created;
    }

    public GameObject getTarget(){
        return (GameObject)source;
    }

    public GameObject getCreated(){
        return created;
    }
}
