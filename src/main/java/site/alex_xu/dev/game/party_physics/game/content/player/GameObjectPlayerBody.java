package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
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
    public GameObject createFromPackage(Package pkg) {

        int id = pkg.getInteger("id");
        double posX = pkg.getFraction("pos.x");
        double posY = pkg.getFraction("pos.y");
        double posA = pkg.getFraction("pos.a");
        double velX = pkg.getFraction("vel.x");
        double velY = pkg.getFraction("vel.y");
        double velA = pkg.getFraction("vel.a");

        GameObject.objectIDCounter = id;
        GameObjectPlayerBody body = new GameObjectPlayerBody(posX, posY);
        body.getTransform().setTranslation(posX, posY);
        body.getTransform().setRotation(posA);
        body.setLinearVelocity(velX, velY);
        body.setAngularVelocity(velA);

        return body;
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.setColor(color);
        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());
        renderer.rect(-w / 2, -h / 2, w, h / 2, 0.2);
        renderer.rect(-w / 2, -0.25, w, h / 2 + 0.25, 0.2);
        renderer.popState();
    }
}
