package site.alex_xu.dev.game.party_physics.game.content.objects.projectile;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectProjectile;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class LiteBullet extends GameObjectProjectile {

    private static final double width = 0.3;
    private static final double height = 0.05;

    private int hitCount = 0;


    public LiteBullet(Vector2 pos, Vector2 vel) {
        super();
        Rectangle rectangle = new Rectangle(width, height);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFriction(0);
        fixture.setRestitution(0.2);
        setAngularDamping(0);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.003, 0.001));
        getTransform().setTranslation(pos);
        getTransform().setRotation(vel.getDirection());
        setLinearVelocity(vel);
        setBullet(true);
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        renderer.pushState();
        renderer.setColor(Color.YELLOW);
        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());
        renderer.rect(-width / 2, -height / 2, width, height);
        renderer.popState();
    }

    @Override
    public boolean onHit(GameObject object, Vector2 location) {
        if (object instanceof GameObjectPlayerPart && hitCount == 0) {
            object.applyImpulse(Vector2.create(4, getTransform().getRotationAngle()), location);
        }
        hitCount++;
        return hitCount > 3;
    }
}
