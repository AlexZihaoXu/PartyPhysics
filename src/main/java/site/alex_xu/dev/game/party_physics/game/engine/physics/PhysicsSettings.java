package site.alex_xu.dev.game.party_physics.game.engine.physics;

import org.dyn4j.collision.CategoryFilter;

/**
 * A Physics Settings class that keeps all physics simulation settings
 */
public class PhysicsSettings {
    public static final int TICKS_PER_SECOND = 150;

    public static final int SYNCS_PER_SECOND = 30;
    public static final int FORCE_SYNC_PER_SECOND = 10;
    public static final CategoryFilter playerFilter = new CategoryFilter(2, 0);
}
