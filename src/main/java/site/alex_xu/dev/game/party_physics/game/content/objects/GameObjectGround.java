package site.alex_xu.dev.game.party_physics.game.content.objects;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectGround extends GameObject {

    private double x, y, width, height;
    private Color color = new Color(5, 10, 31);

    public GameObjectGround(double x, double y, double w, double h) {
        super();
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        Rectangle rectangle = new Rectangle(w, h);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFriction(0.8);
        addFixture(fixture);

        setMass(MassType.INFINITE);
        translate(x + w / 2, y + h / 2);
    }

    @Override
    public void onTick() {
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.translate(getWorldCenter());
        renderer.setColor(color);
        renderer.rect(-width / 2, -height / 2, width, height);
        renderer.popState();
    }
}
