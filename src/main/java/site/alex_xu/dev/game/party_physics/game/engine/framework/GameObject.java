package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.Body;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public abstract class GameObject extends Body {

    GameWorld world = null;

    public double getDeltaTime() {
        return getWindow().getDeltaTime();
    }

    public double getCurrentTime() {
        return getWindow().getCurrentTime();
    }

    public GameWorld getWorld() {
        return world;
    }

    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    public void onTick() {

    }

    public void onPhysicsTick(double dt) {

    }

    abstract public void onRender(Renderer renderer);
}
