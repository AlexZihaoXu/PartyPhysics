package site.alex_xu.dev.game.party_physics.game.content.particles;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Particle;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;


public class ExplosionSmokeParticle extends Particle {

    Vector2 pos, finalOffset;
    double size;

    public Color color = new Color(60, 60, 60, 225);


    public ExplosionSmokeParticle(Vector2 pos, Vector2 finalOffset, double size) {
        this.size = size;
        this.pos = pos;
        this.finalOffset = finalOffset;
    }

    @Override
    public void onRender(Renderer renderer) {
        double v = 1 - getLifetime();
        Vector2 offset = pos.copy().add(finalOffset.copy().product(1 - v * v * v));

        renderer.pushState();

        renderer.translate(offset);
        renderer.setColor(color.getRed(), color.getGreen(), color.getBlue(), (int) (color.getAlpha() * (v * v * v * v * v)));
        v = 0.5 + v * 0.5;
        renderer.scale(v * v * v * v);

        renderer.circle(0, 0, size);

        renderer.popState();
    }

    @Override
    public boolean shouldDelete() {
        return getLifetime() > 1;
    }
}
