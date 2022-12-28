package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectPlayerBody extends GameObjectPlayerPart {

    Color color = new Color(99, 194, 42, 255);
    static final double w = 0.2, h = 0.82;

    public GameObjectPlayerBody(double x, double y) {
        super();

        Rectangle rectangle = new Rectangle(w, h);
        BodyFixture fixture = new BodyFixture(rectangle);
        CategoryFilter filter = new CategoryFilter(2, 0);
        fixture.setFilter(filter);
        fixture.setFriction(0.1);
        addFixture(fixture);
        setMass(MassType.NORMAL);
        translate(x, y);
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.setColor(color);
        renderer.translate(getWorldCenter());
        renderer.rotate(getTransform().getRotationAngle());
        renderer.rect(-w / 2, -h / 2, w, h/2, 0.2);
        renderer.rect(-w / 2, -0.25, w, h/2 + 0.25, 0.2);
        renderer.popState();
    }
}
