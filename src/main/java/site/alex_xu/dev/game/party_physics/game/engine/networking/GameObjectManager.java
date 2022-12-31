package site.alex_xu.dev.game.party_physics.game.engine.networking;

import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GameObjectManager {
    ArrayList<GameObject> gameObjectMap = new ArrayList<>();
    HashMap<String, Integer> typeIdMap = new HashMap<>();

    private static GameObjectManager INSTANCE = null;

    public static GameObjectManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GameObjectManager();
        }
        return INSTANCE;
    }

    public void register(GameObject object) {
        int id = typeIdMap.size();
        gameObjectMap.add(object);
        typeIdMap.put(object.getClass().getCanonicalName(), id);
    }

    public int getObjectTypeID(GameObject object) {
        if (!typeIdMap.containsKey(object.getClass().getCanonicalName())) {
            register(object);
            System.err.println(object.getClass().getCanonicalName() + " isn't registered!");
        }
        return typeIdMap.get(object.getClass().getCanonicalName());
    }

    public Package createCreationPackage(GameObject object) {
        Package pkg = object.createCreationPackage();
        pkg.setInteger("typeID", getObjectTypeID(object));
        return pkg;
    }

    public GameObject createFromPackage(Package pkg) {
        return gameObjectMap.get(pkg.getInteger("typeID")).createFromPackage(pkg);
    }
}
