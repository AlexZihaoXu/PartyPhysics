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

public class GameWorld {

    WorldCollisionHandler playerCollisionHandler = new WorldCollisionHandler(this);
    World<GameObject> world;
    ArrayList<GameObject> objects = new ArrayList<>();

    HashSet<Particle> particles = new HashSet<>();

    TreeMap<Integer, GameObject> objectsIdMap = new TreeMap<>();

    private final Body staticBody = new Body();

    private final Object lock = new Object();

    private final ArrayList<Particle> addingParticles = new ArrayList<>();
    long updateCount = 0;

    private int alivePlayerCount = 0;

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

    public void addParticle(Particle particle) {
        particle.world = this;
        addingParticles.add(particle);
    }

    public int getAlivePlayerCount() {
        return alivePlayerCount;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public void addPlayer(Player player) {
        synchronized (lock) {
            player.initPhysics(this);
            players.put(player.getID(), player);
            if (player.isAlive())
                alivePlayerCount += 1;
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

    public boolean shouldStartNextRound() {
        return getAlivePlayerCount() <= 1;
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
