package site.alex_xu.dev.game.party_physics.game.content.particles;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Particle;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

/**
 * The particle when player was hit by a bullet
 */
public class PlayerHitParticle extends Particle {
    private final Vector2 vel;
    private final Vector2 centerPos = new Vector2();

    private final double size, width;

    private final Color color;

    public double lifetimeScale = 1;

    public PlayerHitParticle(Color color, double x, double y, double direction, double width, double magnitude, double size) {
        this.color = color;
        centerPos.set(x, y);
        vel = Vector2.create(magnitude, direction);
        this.size = size;
        this.width = width;
    }


    /**
     * @return the progress of the animation from 0 to 1
     */
    public double getProgress() {
        return Math.min(1, getLifetime() * 1.5 / lifetimeScale);
    }

    @Override
    public void onRender(Renderer renderer) {

        renderer.pushState();


        double v = getProgress();
        double alpha = 1 - v * v * v * v * v;
        double scale = (1 - v * v * v * 0.8) * size;
        double width = (2 - v * v * v) * this.width;
        double direction = vel.getDirection();
        v = 1 - v;
        double distance = (1 - v * v) * vel.getMagnitude();

        renderer.setColor(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * color.getAlpha()));
        renderer.translate(centerPos);
        renderer.rotate(direction);
        renderer.translate(distance, 0);
        renderer.scale(scale);
        renderer.scale(width, 1);
        renderer.circle(0, 0, 1);

        renderer.popState();
    }

    @Override
    public boolean shouldDelete() {
        return getProgress() >= 1;
    }
}
