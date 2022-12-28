package site.alex_xu.dev.game.party_physics.game.content.objects;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectBox extends GameObject {
    private final Color color = new Color(197, 161, 65);
    public static final double size = 1;

    public GameObjectBox(double x, double y) {
        super();


        Rectangle rectangle = new Rectangle(size, size);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFriction(0.2);
//        fixture.setRestitutionVelocity(1);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.2, 0.3));
        translate(x + size / 2, y + size / 2);
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.translate(getWorldCenter());
        renderer.rotate(getTransform().getRotationAngle());

        renderer.setColor(color);
        renderer.rect(-size / 2, -size / 2, size, size);
        renderer.setColor(color.darker());
        renderer.rect(-size / 2 * 0.8, -size / 2 * 0.8, size * 0.8, size * 0.8);
        renderer.setColor(color);
        renderer.setLineWidth(size * 0.05);
        renderer.line(-size / 2 * 0.75, -size / 2 * 0.75, size / 2 * 0.75, size / 2 * 0.75);

        renderer.popState();
    }
}
