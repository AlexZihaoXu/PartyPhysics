package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;
import org.dyn4j.world.World;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeMap;

public class GameWorld {

    WorldCollisionHandler playerCollisionHandler = new WorldCollisionHandler(this);
    World<GameObject> world;
    ArrayList<GameObject> objects = new ArrayList<>();

    TreeMap<Integer, GameObject> objectsIdMap = new TreeMap<>();

    private final Body staticBody = new Body();

    private final Object lock = new Object();
    long updateCount = 0;

    TreeMap<Integer, Player> players = new TreeMap<>();

    public GameWorld() {
    }

    public void init() {
        updateCount = (long) (getCurrentTime() / (1d / PhysicsSettings.TICKS_PER_SECOND));
        world = new World<>();
        world.setGravity(0, 9.8 * 3);
        world.addContactListener(playerCollisionHandler);

        staticBody.setMass(MassType.INFINITE);
    }

    public void addPlayer(Player player) {
        synchronized (lock) {
            player.initPhysics(this);
            players.put(player.getID(), player);
        }
    }

    public void removePlayer(Player player) {
        synchronized (lock) {
            player.offloadPhysics(this);
            players.remove(player.getID());
        }
    }

    public boolean hasPlayer(int id) {
        return players.containsKey(id);
    }

    public Body getStaticBody() {
        return staticBody;
    }

    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    public double getDeltaTime() {
        return getWindow().getDeltaTime();
    }

    public double getCurrentTime() {
        return getWindow().getCurrentTime();
    }

    public World<GameObject> getSimulatedWorld() {
        return world;
    }

    public int getObjectsCount() {
        return objects.size();
    }

    public void onRender(Renderer renderer) {
        synchronized (lock) {
            for (GameObject object : objects) {
                if (!(object instanceof GameObjectPlayerPart)) {
                    object.onRender(renderer);
                }
            }
            for (Player player : players.values()) {
                player.onRender(renderer);
            }
        }
    }

    public void onTick() {

        synchronized (lock) {
            double dt = 1d / PhysicsSettings.TICKS_PER_SECOND;
            long expectedUpdateCount = (long) (getCurrentTime() / dt);
            while (updateCount < expectedUpdateCount) {
                playerCollisionHandler.now = updateCount * dt;
                world.updatev(dt);
                ArrayList<GameObject> objects = new ArrayList<>(this.objects);
                for (GameObject object : objects) {
                    object.onPhysicsTick(dt);
                }
                for (Player player : players.values()) {
                    player.onPhysicsTick(dt, updateCount * dt);
                    for (Player p : players.values()) {
                        if (p != player) {
                            player.tickPlayers(p);
                        }
                    }
                }
                updateCount++;
            }

            for (GameObject object : objects) {
                object.onTick();
            }
            for (GameObject object : objects) {
                object.onTickAnimation();
            }
        }

    }

    public Player getPlayer(int id) {
        return players.get(id);
    }

    public void addObject(GameObject object) {
        synchronized (lock) {
            if (object.world != null)
                throw new IllegalStateException("Attempted to add an object that has already been added to a world!");
            object.world = this;
            objects.add(object);
            objectsIdMap.put(object.getObjectID(), object);
            world.addBody(object);
            object.getRenderPos().set(object.getTransform().getTranslation());
        }
    }

    public void syncObject(Package pkg) {
        synchronized (lock) {
            int id = pkg.getInteger("id");
            if (objectsIdMap.containsKey(id))
                objectsIdMap.get(id).syncFromPackage(pkg);
        }
    }

    public ArrayList<GameObject> getObjects() {
        return objects;
    }

    public boolean hasObject(GameObject object) {
        return object.world == this;
    }

    public boolean hasObject(int id) {
        return objectsIdMap.containsKey(id);
    }

    public void removeObject(GameObject object) {
        if (object.world != this) {
            if (object.world == null)
                throw new RuntimeException("Attempted to remove an object that was not added!");
            throw new RuntimeException("Attempted to remove an object that doesn't belong to this world!");
        }
        objects.remove(object);
        objectsIdMap.remove(object.getObjectID());
        world.removeBody(object);
    }

    public int getUpdateCount() {
        return (int) updateCount;
    }

    public void setUpdateCount(int value) {
        updateCount = value;
    }

    public GameObject getObject(int objID) {
        return objectsIdMap.get(objID);
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }
}
