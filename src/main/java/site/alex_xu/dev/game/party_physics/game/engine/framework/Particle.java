package site.alex_xu.dev.game.party_physics.game.engine.framework;

import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

/**
 * A base class for all particles
 */
public abstract class Particle {
    double lifeTime = 0;

    /**
     * @return the current lifetime of the particle
     */
    public double getLifetime() {
        return lifeTime;
    }

    GameWorld world;

    /**
     * @return the world where this particle lives in
     */
    public GameWorld getWorld() {
        return world;
    }

    /**
     * Render this particle using the renderer
     * @param renderer the renderer to render
     */
    public abstract void onRender(Renderer renderer);

    /**
     * @param dt the delta time between last tick and current tick
     */
    public void onPhysicsTick(double dt) {

    }

    /**
     * @return true if this particle has finished playing and should be deleted
     */
    public abstract boolean shouldDelete();
}
