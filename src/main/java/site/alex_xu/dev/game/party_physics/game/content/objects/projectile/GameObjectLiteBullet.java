package site.alex_xu.dev.game.party_physics.game.content.objects.projectile;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectProjectile;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class GameObjectLiteBullet extends GameObjectProjectile {

    private static final double width = 0.3;
    private static final double height = 0.05;

    private int hitCount = 0;


    public GameObjectLiteBullet(Vector2 pos, Vector2 vel) {
        super();
        Rectangle rectangle = new Rectangle(width, height);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFriction(0);
        fixture.setRestitution(0.2);
        setAngularDamping(0);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.003, 0.001));
        getTransform().setTranslation(pos);
        getTransform().setRotation(vel.getDirection());
        setLinearVelocity(vel);
        setBullet(true);
    }

    @Override
    public Package createCreationPackage() {
        Package pkg = super.createCreationPackage();
        pkg.setInteger("hit", hitCount);
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
        int hit = pkg.getInteger("hit");

        GameObject.nextObjectID = id;
        GameObjectLiteBullet bullet = new GameObjectLiteBullet(new Vector2(), new Vector2());
        bullet.getTransform().setTranslation(posX, posY);
        bullet.getTransform().setRotation(posA);
        bullet.setLinearVelocity(velX, velY);
        bullet.setAngularVelocity(velA);
        bullet.hitCount = hit;
        return bullet;
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        renderer.pushState();
        renderer.setColor(Color.YELLOW);
        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());
        renderer.rect(-width / 2, -height / 2, width, height);
        renderer.popState();
    }

    @Override
    public boolean onHit(GameObject object, Vector2 location) {
        if (object instanceof GameObjectPlayerPart && hitCount == 0) {
            object.applyImpulse(Vector2.create(4, getTransform().getRotationAngle()), location);
        }
        hitCount++;
        return hitCount > 3;
    }
}
