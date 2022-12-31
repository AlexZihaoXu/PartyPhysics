package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.broadphase.CollisionBodyBroadphaseFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectPlayerLimb extends GameObjectPlayerPart {

    static double r = 0.05;
    double length;

    public GameObjectPlayerLimb(double x1, double y1, double x2, double y2) {
        super();

        length = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        org.dyn4j.geometry.Rectangle shape = new Rectangle(length + r * 2, r);
        BodyFixture fixture = new BodyFixture(shape);
        fixture.setFilter(PhysicsSettings.playerFilter);
        fixture.setFriction(0.9);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.2, 0.03));
        rotate(Math.atan2(y2 - y1, x2 - x1));
        translate((x1 + x2) / 2, (y1 + y2) / 2);
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.setColor(color);
        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());
        renderer.rect(-length / 2 - r, -r, length + r * 2, r * 2, r);
        renderer.popState();
    }
}
