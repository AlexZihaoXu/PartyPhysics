package site.alex_xu.dev.game.party_physics.game.engine.physics;

import org.dyn4j.collision.CategoryFilter;

public class PhysicsSettings {
    public static final int TICKS_PER_SECOND = 120;

    public static final int SYNCS_PER_SECOND = 4;
    public static final CategoryFilter playerFilter = new CategoryFilter(2, 0);
}
