package site.alex_xu.dev.game.party_physics.game.content.particles;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Particle;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class PlayerHitParticle extends Particle {

    public PlayerHitParticle() {

    }

    @Override
    public void onRender(Renderer renderer) {

    }

    public static void draw(Renderer renderer, Vector2 p1, double s1, Vector2 p2, double s2) {
        {
            Vector2 offset = Vector2.create(s1, p1.getAngleBetween(p2) + Math.PI / 2);
            renderer.triangle(p1.x, p1.y, p2.x, p2.y, p1.x + offset.x, p1.y + offset.y);
        }
    }

    @Override
    public boolean shouldDelete() {
        return getLifetime() > 1;
    }
}
