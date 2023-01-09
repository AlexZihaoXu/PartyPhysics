package site.alex_xu.dev.game.party_physics.game.engine.framework;

import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

public abstract class Particle {
    double lifeTime = 0;

    public double getLifetime() {
        return lifeTime;
    }

    GameWorld world;

    public GameWorld getWorld() {
        return world;
    }

    public abstract void onRender(Renderer renderer);

    public void onPhysicsTick(double dt) {

    }

    public abstract boolean shouldDelete();
}
