package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.broadphase.CollisionBodyBroadphaseFilter;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectPlayerLimb extends GameObjectPlayerPart {

    static double r = 0.05;
    double length;

    double w, h;

    public GameObjectPlayerLimb(double x1, double y1, double x2, double y2) {
        super();
        length = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        w = length + r * 2;
        h = r;
        org.dyn4j.geometry.Rectangle shape = new Rectangle(w, h);
        BodyFixture fixture = new BodyFixture(shape);
        fixture.setFilter(PhysicsSettings.playerFilter);
        fixture.setFriction(0.9);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.2, 0.03));
        rotate(Math.atan2(y2 - y1, x2 - x1));
        translate((x1 + x2) / 2, (y1 + y2) / 2);
    }

    public GameObjectPlayerLimb(double w, double h) {
        super();
        this.w = w;
        this.h = h;

        org.dyn4j.geometry.Rectangle shape = new Rectangle(w, h);
        BodyFixture fixture = new BodyFixture(shape);
        fixture.setFilter(PhysicsSettings.playerFilter);
        fixture.setFriction(0.9);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.2, 0.03));
    }

    @Override
    public Package createCreationPackage() {
        Package pkg = super.createCreationPackage();
        pkg.setFraction("w", w);
        pkg.setFraction("h", h);
        return pkg;
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
        double w = pkg.getFraction("w");
        double h = pkg.getFraction("h");

        GameObject.objectIDCounter = id;
        GameObjectPlayerLimb limb = new GameObjectPlayerLimb(w, h);
        limb.getTransform().setTranslation(posX, posY);
        limb.getTransform().setRotation(posA);
        limb.setLinearVelocity(velX, velY);
        limb.setAngularVelocity(velA);

        return limb;
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
