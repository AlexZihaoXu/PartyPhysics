package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class GameObjectPlayerFoot extends GameObjectPlayerPart {

    public GameObjectPlayerFoot(double x, double y) {
        super();

        Circle circle = new Circle(0.2);
        BodyFixture fixture = new BodyFixture(circle);
        fixture.setFilter(PhysicsSettings.playerFilter);
        fixture.setFriction(0.9);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.001, 0.03));
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

        GameObject.nextObjectID = id;
        GameObjectPlayerFoot foot = new GameObjectPlayerFoot(posX, posY);
        foot.getTransform().setTranslation(posX, posY);
        foot.getTransform().setRotation(posA);
        foot.setLinearVelocity(velX, velY);
        foot.setAngularVelocity(velA);
        return foot;
    }

    @Override
    public void onRender(Renderer renderer) {
    }
}
