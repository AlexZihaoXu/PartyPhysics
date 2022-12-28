package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class GameObjectPlayerFoot extends GameObjectPlayerPart{

    public GameObjectPlayerFoot(double x, double y) {
        super();

        Circle circle = new Circle(0.2);
        BodyFixture fixture = new BodyFixture(circle);
        CategoryFilter filter = new CategoryFilter(2, 0);
        fixture.setFilter(filter);
        fixture.setFriction(0.9);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.001, 0.03));
        translate(x, y);
    }
    @Override
    public void onRender(Renderer renderer) {
    }
}
