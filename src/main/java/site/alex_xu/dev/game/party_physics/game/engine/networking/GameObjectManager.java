package site.alex_xu.dev.game.party_physics.game.engine.networking;

import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Game Object Manager class
 * A class that helps with registering new game objects, assigning IDs and stuff..
 */
public class GameObjectManager {
    /**
     * Stores the instances of registered objects
     */
    ArrayList<GameObject> gameObjectMap = new ArrayList<>();
    /**
     * Stores the class path of the object type and an instance of the type of the object in pairs
     * Used later for checking whether an object is registered or not
     */
    HashMap<String, Integer> typeIdMap = new HashMap<>();

    /**
     * The only instance of the Game Object Manager class (Singleton)
     */
    private static GameObjectManager INSTANCE = null;

    /**
     * @return the instance of Game Object Manager (creates one if it does not exist)
     */
    public static GameObjectManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GameObjectManager();
        }
        return INSTANCE;
    }

    /**
     * @param object a new object to be registered
     */
    public void register(GameObject object) {
        int id = typeIdMap.size();
        gameObjectMap.add(object);
        typeIdMap.put(object.getClass().getCanonicalName(), id);
    }

    /**
     * @param object the object to get type ID
     * @return the type ID of the given object
     */
    public int getObjectTypeID(GameObject object) {
        if (!typeIdMap.containsKey(object.getClass().getCanonicalName())) {
            register(object);
            System.err.println(object.getClass().getCanonicalName() + " isn't registered!");
        }
        return typeIdMap.get(object.getClass().getCanonicalName());
    }

    /**
     * @param object the object to create package
     * @return a network package that stores instructions and information for the object creation
     */
    public Package createCreationPackage(GameObject object) {
        Package pkg = object.createCreationPackage();
        pkg.setInteger("typeID", getObjectTypeID(object));
        return pkg;
    }

    /**
     * @param player the player to create package
     * @return a network package that stores information of a players status for player creation
     */
    public Package createCreationPackage(Player player) {
        Package pkg = new Package(PackageTypes.WORLD_SYNC_ADD_PLAYER);
        Color color = player.getColor();
        pkg.setInteger("id", player.head.getObjectID());
        pkg.setInteger("playerID", player.getID());
        pkg.setInteger("color", color.getRGB());
        pkg.setFraction("x", player.head.getTransform().getTranslationX());
        pkg.setFraction("y", player.head.getTransform().getTranslationY());
        return pkg;
    }

    /**
     * @param pkg the package to deserialize and create object
     * @return the created object based on the info given in the package
     */
    public GameObject createFromPackage(Package pkg) {
        return gameObjectMap.get(pkg.getInteger("typeID")).createFromPackage(pkg);
    }

    /**
     * @param pkg the package to deserialize and create player
     * @return the player instance created based on the given package
     */
    public Player createPlayerFromPackage(Package pkg) {
        int id = pkg.getInteger("id");
        int color = pkg.getInteger("color");
        int playerID = pkg.getInteger("playerID");
        double x = pkg.getFraction("x");
        double y = pkg.getFraction("y");
        GameObject.nextObjectID = id;
        return new Player(new Color(color), x, y, playerID);
    }
}
