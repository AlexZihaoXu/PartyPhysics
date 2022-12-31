package site.alex_xu.dev.game.party_physics.game.content.objects.items;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectItem;
import site.alex_xu.dev.game.party_physics.game.content.objects.projectile.LiteBullet;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class GameObjectItemSMG extends GameObjectItem {

    private Triangle[] triangles;
    private double lastShootTime = 0;

    public GameObjectItemSMG(double x, double y) {
        super();
        updateModel(false);
        translate(x, y);
    }

    @Override
    protected void updateModel(boolean flipped) {
        this.removeAllFixtures();
        triangles = flipped ? getFlippedModel("models/gun/smg.mdl") : getModel("models/gun/smg.mdl");
        for (Triangle triangle : triangles) {
            BodyFixture fixture = new BodyFixture(triangle);
            if (isHoldByPlayer())
                fixture.setFilter(PhysicsSettings.playerFilter);
            fixture.setFriction(0.5);
            addFixture(fixture);
        }
        if (isHoldByPlayer()) {
            setAngularDamping(0.3);
        } else {
            setAngularDamping(15);
        }
        setMass(new Mass(new Vector2(0, 0), 0.01, 0.001));
    }

    @Override
    public void onUse(Player user) {
        double now = getPhysicsTime();
        if (now - lastShootTime > 1 / 12d) {
            lastShootTime = now;
            Vector2 vel = Vector2.create(60, getTransform().getRotationAngle());
            LiteBullet bullet = new LiteBullet(getWorldPoint(new Vector2(0.26, 0.15 * (isFlipped() ? 1 : -1))), vel);
            getWorld().addObject(bullet);
            user.body.applyImpulse(Vector2.create(-2, getTransform().getRotationAngle() + (Math.random() - 0.5) * 0.1));
        }
    }
    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.setColor(70);
        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());
        for (Triangle triangle : triangles)
            renderer.triangle(triangle);
        renderer.popState();
    }
}
