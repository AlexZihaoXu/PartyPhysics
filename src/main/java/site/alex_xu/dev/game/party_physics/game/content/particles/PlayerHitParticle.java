package site.alex_xu.dev.game.party_physics.game.content.particles;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Particle;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

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

    public static void draw(Renderer renderer, Vector2 p1, double s1, Vector2 p2, double s2) {
        renderer.setColor(Color.white);

        renderer.circle(p1, s1);
        renderer.circle(p2, s2);

        double direction = Math.atan2(p1.y - p2.y, p1.x - p2.x) + Math.PI / 2;

        Vector2 offset1 = Vector2.create(s1, direction);
        Vector2 offset2 = Vector2.create(s2, direction);
        Vector2 v1 = p1.copy().add(offset1);
        Vector2 v2 = p1.copy().subtract(offset1);
        Vector2 v3 = p2.copy().add(offset2);
        Vector2 v4 = p2.copy().subtract(offset2);

        renderer.triangle(v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
        renderer.triangle(v2.x, v2.y, v3.x, v3.y, v4.x, v4.y);

    }

    @Override
    public boolean shouldDelete() {
        return getProgress() >= 1;
    }
}
