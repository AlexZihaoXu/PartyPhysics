package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectPlayerBody extends GameObjectPlayerPart {

    static final double w = 0.2, h = 0.82;

    public GameObjectPlayerBody(double x, double y) {
        super();

        Rectangle rectangle = new Rectangle(w, h);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFilter(PhysicsSettings.playerFilter);
        fixture.setFriction(0.1);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.2, 0.03));
        translate(x, y);
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.setColor(color);
        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());
        renderer.rect(-w / 2, -h / 2, w, h/2, 0.2);
        renderer.rect(-w / 2, -0.25, w, h/2 + 0.25, 0.2);
        renderer.popState();
    }
}
