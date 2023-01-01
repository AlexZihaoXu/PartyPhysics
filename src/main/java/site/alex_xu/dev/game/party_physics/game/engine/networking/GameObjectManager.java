package site.alex_xu.dev.game.party_physics.game.engine.networking;

import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;

import java.awt.*;
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

    public Package createCreationPackage(Player player) {
        Package pkg = new Package(PackageTypes.PHYSICS_SYNC_GAME_PLAYER_CREATE);
        Color color = player.getColor();
        pkg.setInteger("id", player.head.getObjectID());
        pkg.setInteger("playerID", player.getID());
        pkg.setInteger("color", color.getRGB());
        pkg.setFraction("x", player.head.getTransform().getTranslationX());
        pkg.setFraction("y", player.head.getTransform().getTranslationY());
        return pkg;
    }

    public GameObject createFromPackage(Package pkg) {
        return gameObjectMap.get(pkg.getInteger("typeID")).createFromPackage(pkg);
    }

    public Player createPlayerFromPackage(Package pkg) {
        int id = pkg.getInteger("id");
        int color = pkg.getInteger("color");
        int playerID = pkg.getInteger("playerID");
        double x = pkg.getFraction("x");
        double y = pkg.getFraction("y");
        GameObject.objectIDCounter = id;
        return new Player(new Color(color), x, y, playerID);
    }
}
