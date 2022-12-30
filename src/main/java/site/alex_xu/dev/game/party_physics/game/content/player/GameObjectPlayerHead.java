package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectPlayerHead extends GameObjectPlayerPart {
    static final double r = 0.22;
    public GameObjectPlayerHead(double x, double y) {
        super();

        Circle circle = new Circle(r);
        BodyFixture fixture = new BodyFixture(circle);
        CategoryFilter filter = new CategoryFilter(2, 0);
        fixture.setFilter(filter);
        fixture.setFriction(0.1);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.2, 0.03));
        translate(x, y);
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.setColor(color);
        renderer.circle(getRenderPos(), r);
        renderer.setColor(color.darker());
        renderer.popState();
    }
}
