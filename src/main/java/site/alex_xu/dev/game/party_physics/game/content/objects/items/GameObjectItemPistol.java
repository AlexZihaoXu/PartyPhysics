package site.alex_xu.dev.game.party_physics.game.content.objects.items;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectItem;
import site.alex_xu.dev.game.party_physics.game.content.objects.projectile.GameObjectLiteBullet;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class GameObjectItemPistol extends GameObjectItem {

    private Triangle[] triangles;
    private double lastShootTime = 0;

    public GameObjectItemPistol(double x, double y) {
        this(x, y, false);
    }

    GameObjectItemPistol(double x, double y, boolean flipped) {
        super();
        updateModel(flipped);
        translate(x, y);
    }

    @Override
    protected void updateModel(boolean flipped) {
        this.removeAllFixtures();
        triangles = flipped ? getFlippedModel("models/gun/pistol.mdl") : getModel("models/gun/pistol.mdl");
        for (Triangle triangle : triangles) {
            BodyFixture fixture = new BodyFixture(triangle);
            if (isHoldByPlayer())
                fixture.setFilter(PhysicsSettings.playerFilter);
            fixture.setFriction(0.5);
            addFixture(fixture);
        }
        if (isHoldByPlayer()) {
            setAngularDamping(0.1);
        } else {
            setAngularDamping(10);
        }
        setMass(new Mass(new Vector2(0, 0), 0.01, 0.001));
    }

    @Override
    public void onUse(Player user) {
        double now = getPhysicsTime();
        if (now - lastShootTime > 1 / 4d) {
            lastShootTime = now;
            if (isHostSide()) {
                Vector2 vel = Vector2.create(80, getTransform().getRotationAngle());
                GameObjectLiteBullet bullet = new GameObjectLiteBullet(getWorldPoint(new Vector2(0.24, 0.15 * (isFlipped() ? 1 : -1))), vel);
                serverSideWorldSyncer.syncAddObject(bullet);
                serverSideWorldSyncer.syncAddCameraShake(-9, getTransform().getRotationAngle() + (Math.random() - 0.5) * Math.PI, 40, true);
                user.body.applyImpulse(Vector2.create(-5, getTransform().getRotationAngle()));
            }
        }
    }

    @Override
    public Package createCreationPackage() {
        Package pkg = super.createCreationPackage();
        pkg.setBoolean("flip", isFlipped());
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
        boolean flipped = pkg.getBoolean("flip");

        GameObject.nextObjectID = id;
        GameObjectItemPistol pistol = new GameObjectItemPistol(posX, posY, flipped);
        pistol.getTransform().setTranslation(posX, posY);
        pistol.getTransform().setRotation(posA);
        pistol.setLinearVelocity(velX, velY);
        pistol.setAngularVelocity(velA);
        return pistol;
    }

    @Override
    public void onRender(Renderer renderer) {
        renderer.pushState();
        renderer.setColor(70);
        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());
        for (Triangle triangle : triangles)
            renderer.triangle(triangle);
        renderer.popState();
    }
}
