package site.alex_xu.dev.game.party_physics.game.content.particles;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Particle;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

/**
 * The particle when bullet hit anything
 */
public class HitParticle extends Particle {
    private final Vector2 pos = new Vector2();
    private final Vector2 vel = new Vector2();

    private final Color color;

    private final int type;

    private double angle = 0;

    private double angularVel = 0;

    public HitParticle(Color color, double x, double y, double vx, double vy) {
        pos.set(x, y);
        vel.set(vx, vy);
        type = (int) (Math.random() * 3);
        angle = Math.random() * Math.PI * 2;
        angularVel = Math.random() - 0.5;
        this.color = color;
    }

    /**
     * @return the animation progress from 0 to 1
     */
    private double getProgress() {
        return Math.min(1, getLifetime() / 1.2);
    }

    @Override
    public void onRender(Renderer renderer) {
        double progress = getProgress();
        double v = (1 - progress);
        int alpha = (int) (v * v * color.getAlpha());

        renderer.pushState();
        renderer.translate(pos);
        renderer.setColor(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        renderer.scale(0.02 * v * v * v * v + 0.02);
        renderer.rotate(angle);
        if (type == 0) {
            renderer.rect(-1, -1, 2, 2, 1.4);
        } else if (type == 1) {
            renderer.triangle(0, -1, -1, 0, 1, 1);
        } else {
            renderer.rect(-1, -1, 2, 2, 1.8);
        }
        renderer.popState();
    }

    @Override
    public void onPhysicsTick(double dt) {
        super.onPhysicsTick(dt);
        vel.y += dt * 9.8;
        double progress = getProgress();
        double v = (1 - progress);
        angle += (angularVel * v * v) * dt;

        pos.x += (vel.x * v * v) * dt;
        pos.y += vel.y * dt;
    }

    @Override
    public boolean shouldDelete() {
        return getProgress() >= 1;
    }
}
