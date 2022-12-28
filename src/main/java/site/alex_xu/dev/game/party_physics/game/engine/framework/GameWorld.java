package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.MassType;
import org.dyn4j.world.World;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GameWorld {

    PlayerCollisionHandler playerCollisionHandler = new PlayerCollisionHandler(this);
    World<GameObject> world;
    ArrayList<GameObject> objects = new ArrayList<>();

    private final Body staticBody = new Body();
    long updateCount = 0;

    ArrayList<Player> players = new ArrayList<>();

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
        players.add(player);
        player.initPhysics(this);
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
        for (GameObject object : objects) {
            if (!(object instanceof GameObjectPlayerPart)) {
                object.onRender(renderer);
            }
        }
        for (Player player : players) {
            player.onRender(renderer);
        }
    }

    public void onTick() {

        double dt = 1d / PhysicsSettings.TICKS_PER_SECOND;
        long expectedUpdateCount = (long) (getCurrentTime() / dt);
        while (updateCount < expectedUpdateCount) {
            playerCollisionHandler.now = updateCount * dt;
            world.updatev(dt);
            for (GameObject object : objects) {
                object.onPhysicsTick(dt);
            }
            for (Player player : players) {
                player.onPhysicsTick(dt, updateCount * dt);
            }
            updateCount++;
        }

        for (GameObject object : objects) {
            object.onTick();
        }

    }

    public void addObject(GameObject object) {
        if (object.world != null)
            throw new IllegalStateException("Attempted to add an object that has already been added to a world!");
        object.world = this;
        objects.add(object);
        world.addBody(object);
    }

    public boolean hasObject(GameObject object) {
        return object.world == this;
    }

    public void removeObject(GameObject object) {
        if (object.world != this) {
            if (object.world == null)
                throw new RuntimeException("Attempted to remove an object that was not added!");
            throw new RuntimeException("Attempted to remove an object that doesn't belong to this world!");
        }
        objects.remove(object);
        world.removeBody(object);
    }
}
