package site.alex_xu.dev.game.party_physics.game.content.objects;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectWoodPlank extends GameObject {

    private final Color color = new Color(197, 161, 65);
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public GameObjectWoodPlank(double x, double y, double w, double h) {
        super();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        Rectangle rectangle = new Rectangle(w, h);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFriction(0.4);
        addFixture(fixture);

        setMass(new Mass(new Vector2(0, 0), 1, 0.5));
        translate(x + w / 2, y + h / 2);
    }

    @Override
    public void onTick() {

    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.translate(getWorldCenter());
        renderer.rotate(getTransform().getRotationAngle());
        renderer.setColor(color);
        renderer.rect(-width / 2, -height / 2, width, height);
        renderer.setColor(color.darker());
        renderer.rect(-width / 2 + 0.1, -height / 2 + 0.1, width - 0.2, height - 0.2);
        renderer.popState();
    }
}
