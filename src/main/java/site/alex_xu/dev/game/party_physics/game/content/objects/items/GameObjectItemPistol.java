package site.alex_xu.dev.game.party_physics.game.content.objects.items;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectItem;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class GameObjectItemPistol extends GameObjectItem {

    private Triangle[] triangles;
    public GameObjectItemPistol(double x, double y) {
        super();
        updateModel(false);
        translate(x, y);
    }



    @Override
    protected void updateModel(boolean flipped) {
        this.removeAllFixtures();
        triangles = flipped ? getFlippedModel("models/gun/pistol.mdl") : getModel("models/gun/pistol.mdl");
        for (Triangle triangle : triangles) {
            BodyFixture fixture = new BodyFixture(triangle);
            if (isHoldByPlayer())
                fixture.setFilter(PhysicsSettings.playerFilter);
            fixture.setFriction(0.5);
            addFixture(fixture);
        }
        setAngularDamping(10);
        setMass(new Mass(new Vector2(0, 0), 0.003, 0.01));
    }

    @Override
    public void onUse(Player user) {

    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.setColor(100);
        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());
        for (Triangle triangle : triangles)
            renderer.triangle(triangle);
        renderer.popState();
    }
}
