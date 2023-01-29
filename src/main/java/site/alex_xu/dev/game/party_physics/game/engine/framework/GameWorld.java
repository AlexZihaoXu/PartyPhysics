package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;
import org.dyn4j.world.World;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectTNT;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.util.*;

/**
 * A wrap up of DYN4J's World
 * Simulates physics
 */
public class GameWorld {

    WorldCollisionHandler playerCollisionHandler = new WorldCollisionHandler(this);
    World<GameObject> world;
    ArrayList<GameObject> objects = new ArrayList<>();

    HashSet<Particle> particles = new HashSet<>();

    TreeMap<Integer, GameObject> objectsIdMap = new TreeMap<>();

    private final Body staticBody = new Body(); // not used yet (Will be used for creating joints that connects to background)

    private final Object lock = new Object(); // For thread-safe purpose

    private final ArrayList<Particle> addingParticles = new ArrayList<>();
    long updateCount = 0;

    private int alivePlayerCount = 0;

    /**
     * Player ID to Player object map
     */
    TreeMap<Integer, Player> players = new TreeMap<>();

    public GameWorld() {
    }

    /**
     * Create and initialize the world
     */
    public void init() {
        updateCount = (long) (getCurrentTime() / (1d / PhysicsSettings.TICKS_PER_SECOND));
        world = new World<>();
        world.setGravity(0, 9.8 * 3);
        world.addContactListener(playerCollisionHandler);

        staticBody.setMass(MassType.INFINITE);
    }

    /**
     * @param particle new particle
     */
    public void addParticle(Particle particle) {
        particle.world = this;
        addingParticles.add(particle);
    }

    /**
     * @return the number of alive players
     */
    public int getAlivePlayerCount() {
        return alivePlayerCount;
    }

    /**
     * @return the number of total players
     */
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * @param player new player
     */
    public void addPlayer(Player player) {
        synchronized (lock) {
            player.initPhysics(this);
            players.put(player.getID(), player);
            if (player.isAlive())
                alivePlayerCount += 1;
        }
    }

    /**
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        synchronized (lock) {
            player.offloadPhysics(this);
            players.remove(player.getID());
        }
    }

    /**
     * @param id the ID of the player
     * @return true if this player exists, otherwise false
     */
    public boolean hasPlayer(int id) {
        return players.containsKey(id);
    }

    /**
     * @return the static body of the world (not used yet)
     */
    public Body getStaticBody() {
        return staticBody;
    }

    /**
     * @return the window instance of the game
     */
    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    /**
     * @return the delta time between last frame and current frame
     */
    public double getDeltaTime() {
        return getWindow().getDeltaTime();
    }

    /**
     * @return current time in seconds
     */
    public double getCurrentTime() {
        return getWindow().getCurrentTime();
    }

    /**
     * @return the actual DYN4J world
     */
    public World<GameObject> getSimulatedWorld() {
        return world;
    }

    /**
     * @return the number of objects in this world
     */
    public int getObjectsCount() {
        return objects.size();
    }

    /**
     * Draws all objects using the given renderer
     * @param renderer renderer to render
     */
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
        ArrayList<Particle> removed = new ArrayList<>();
        for (Particle particle : particles) {
            particle.onRender(renderer);
            if (particle.shouldDelete())
                removed.add(particle);
        }

        for (Particle particle : removed) {
            particles.remove(particle);
        }
    }

    /**
     * Updates the world based on a fixed TPS: PhysicsSettings.TICKS_PER_SECOND (for stable sync purpose)
     */
    public void onTick() {

        synchronized (lock) {
            particles.addAll(addingParticles);
            addingParticles.clear();
            double dt = 1d / PhysicsSettings.TICKS_PER_SECOND;
            long expectedUpdateCount = (long) (getCurrentTime() / dt);

            ArrayList<GameObject> removed = new ArrayList<>();

            for (GameObject object : getObjects()) {
                if (object instanceof GameObjectTNT) {
                    if (((GameObjectTNT) object).isExploded()) {
                        removed.add(object);
                    }
                }
            }

            for (GameObject obj : removed) {
                removeObject(obj);
            }

            while (updateCount < expectedUpdateCount) {
                playerCollisionHandler.now = updateCount * dt;
                world.updatev(dt);
                ArrayList<GameObject> objects = new ArrayList<>(this.objects);
                for (GameObject object : objects) {
                    object.onPhysicsTick(dt);
                }
                int aliveCount = 0;
                for (Player player : players.values()) {
                    if (!player.isDead()) {
                        aliveCount++;
                    }
                    player.onPhysicsTick(dt, updateCount * dt);
                    for (Player p : players.values()) {
                        if (p != player) {
                            player.tickPlayers(p);
                        }
                    }
                }
                this.alivePlayerCount = aliveCount;
                for (Particle particle : particles) {
                    particle.onPhysicsTick(dt);
                    particle.lifeTime += dt;
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

    /**
     * @return the last player standing, null if not exist
     */
    public Player getLastPlayerStanding() {
        if (getAlivePlayerCount() == 1) {
            for (Player value : players.values()) {
                if (value.isAlive()) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * @return true if the current round is ended and should start another round
     */
    public boolean shouldStartNextRound() {
        return getAlivePlayerCount() <= 1;
    }

    /**
     * @param id the ID of the player
     * @return the player with the given ID
     */
    public Player getPlayer(int id) {
        return players.get(id);
    }

    /**
     * @param object the object to add
     */
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

    /**
     * Syncs an object from the given package
     * @param pkg a package that contains object sync information
     */
    public void syncObject(Package pkg) {
        synchronized (lock) {
            int id = pkg.getInteger("id");
            if (objectsIdMap.containsKey(id))
                objectsIdMap.get(id).syncFromPackage(pkg);
        }
    }

    /**
     * @return all objects
     */
    public ArrayList<GameObject> getObjects() {
        return objects;
    }

    /**
     * @param object the object to check
     * @return true if it exists, otherwise false
     */
    public boolean hasObject(GameObject object) {
        return object.world == this;
    }

    /**
     * @param id the ID of the object
     * @return true if it exists, otherwise false
     */
    public boolean hasObject(int id) {
        return objectsIdMap.containsKey(id);
    }

    /**
     * @param object the object to remove
     */
    public void removeObject(GameObject object) {
        if (object != null && object.world != this) {
            if (object.world == null)
                throw new RuntimeException("Attempted to remove an object that was not added!");
            throw new RuntimeException("Attempted to remove an object that doesn't belong to this world!");
        }
        if (object == null) return;
        objects.remove(object);
        objectsIdMap.remove(object.getObjectID());
        world.removeBody(object);
    }

    /**
     * @return the current number of updates of the physics engine
     */
    public int getUpdateCount() {
        return (int) updateCount;
    }

    /**
     * @param value the updated number of updates of the physics engine
     */
    public void setUpdateCount(int value) {
        updateCount = value;
    }

    /**
     * @param objID object ID
     * @return the object with the given ID
     */
    public GameObject getObject(int objID) {
        return objectsIdMap.get(objID);
    }

    /**
     * @return all players
     */
    public Collection<Player> getPlayers() {
        return players.values();
    }
}
