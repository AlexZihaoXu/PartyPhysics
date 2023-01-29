package site.alex_xu.dev.game.party_physics.game.content.particles;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Particle;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;

/**
 * TNT Explosion spark particle
 */
public class ExplosionSparkParticle extends Particle {

    private final Vector2 pos, vel;
    GameWorld world;

    Clock clock = new Clock();

    public ExplosionSparkParticle(Vector2 pos, Vector2 vel, GameWorld world) {
        this.world = world;
        this.pos = pos.copy();
        this.vel = vel.copy();
    }

    @Override
    public void onPhysicsTick(double dt) {
        super.onPhysicsTick(dt);

        pos.x += dt * vel.x;
        pos.y += dt * vel.y;

        vel.y += dt * 9.8;

        if (clock.elapsedTime() > 0.1) {
            clock.reset();

            world.addParticle(
                    new ExplosionSmokeParticle(
                            this.pos.copy(),
                            Vector2.create(Math.random() * 2 + 0.3, Math.random() * Math.PI * 2),
                            Math.random() * 0.1 + 0.05
                    )
            );

        }
    }

    /**
     * @param color1 color 1
     * @param color2 color 2
     * @param shift the shift from 1 to 2 (value should be between 0 and 1)
     * @return the mixed color based on the shift
     */
    public static Color map(Color color1, Color color2, double shift) {
        double b = 1 - shift;
        return new Color(
                (int) Math.max(0, Math.min(255, (color1.getRed() * shift + color2.getRed() * b))),
                (int) Math.max(0, Math.min(255, (color1.getGreen() * shift + color2.getGreen() * b))),
                (int) Math.max(0, Math.min(255, (color1.getBlue() * shift + color2.getBlue() * b))),
                (int) Math.max(0, Math.min(255, (color1.getAlpha() * shift + color2.getAlpha() * b)))
        );
    }

    /**
     * @return the progress of the animation from 0 to 1
     */
    private double getProgress() {
        return getLifetime() / 1.5;
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();

        renderer.translate(pos);
        double v = 1 - getProgress() * 0.4;
        Color color = map(Color.red, Color.yellow, 1 - getProgress());
        renderer.setColor(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * (1 - getProgress())));
        renderer.circle(0, 0, .2 * (v * v * v));

        renderer.popState();
    }

    @Override
    public boolean shouldDelete() {
        return getProgress() >= 1;
    }
}
