package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.geometry.MassType;
import org.dyn4j.world.World;
import site.alex_xu.dev.game.party_physics.game.content.level.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.util.HashSet;

public class GameWorld {

    World<GameObject> world;
    HashSet<GameObject> objects = new HashSet<>();
    long updateCount = 0;

    public GameWorld() {
    }

    public void init() {
        updateCount = (long) (getCurrentTime() / (1d / PhysicsSettings.TICKS_PER_SECOND));
        System.out.println(updateCount);
        world = new World<>();
        world.setGravity(0, 9.8);
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

    public void onRender(Renderer renderer) {
        for (GameObject object : objects) {
            object.onRender(renderer);
        }
    }

    public void onTick() {

        long expectedUpdateCount = (long) (getCurrentTime() / (1d / PhysicsSettings.TICKS_PER_SECOND));
        while (updateCount < expectedUpdateCount) {
            world.updatev(1d / PhysicsSettings.TICKS_PER_SECOND);
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
