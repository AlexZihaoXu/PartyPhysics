package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class GameObjectPlayerHead extends GameObjectPlayerPart {
    static final double r = 0.22;

    public GameObjectPlayerHead(double x, double y) {
        super();

        Circle circle = new Circle(r);
        BodyFixture fixture = new BodyFixture(circle);
        fixture.setFilter(PhysicsSettings.playerFilter);
        fixture.setFriction(0.1);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.2, 0.03));
        translate(x, y);
    }

    @Override
    public Package createCreationPackage() {
        Package pkg = new Package(PackageTypes.WORLD_SYNC_ADD_OBJECT);

        pkg.setInteger("id", getObjectID());
        pkg.setFraction("pos.x", getTransform().getTranslationX());
        pkg.setFraction("pos.y", getTransform().getTranslationY());
        pkg.setFraction("pos.a", getTransform().getRotationAngle());
        pkg.setFraction("vel.x", getLinearVelocity().x);
        pkg.setFraction("vel.y", getLinearVelocity().y);
        pkg.setFraction("vel.a", getAngularVelocity());

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

        GameObject.nextObjectID = id;
        GameObjectPlayerHead head = new GameObjectPlayerHead(posX, posY);
        head.getTransform().setTranslation(posX, posY);
        head.getTransform().setRotation(posA);
        head.setLinearVelocity(velX, velY);
        head.setAngularVelocity(velA);

        return head;
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
