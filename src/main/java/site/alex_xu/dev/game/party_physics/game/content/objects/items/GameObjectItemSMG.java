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
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class GameObjectItemSMG extends GameObjectItem {

    private Triangle[] triangles;
    private double lastShootTime = 0;

    public GameObjectItemSMG(double x, double y) {
        this(x, y, false);
    }

    public GameObjectItemSMG(double x, double y, boolean flipped) {
        super();
        updateModel(false);
        translate(x, y);
    }

    @Override
    protected void updateModel(boolean flipped) {
        this.removeAllFixtures();
        triangles = flipped ? getFlippedModel("models/gun/smg.mdl") : getModel("models/gun/smg.mdl");
        for (Triangle triangle : triangles) {
            BodyFixture fixture = new BodyFixture(triangle);
            if (isHoldByPlayer())
                fixture.setFilter(PhysicsSettings.playerFilter);
            fixture.setFriction(0.5);
            addFixture(fixture);
        }
        if (isHoldByPlayer()) {
            setAngularDamping(0.3);
        } else {
            setAngularDamping(15);
        }
        setMass(new Mass(new Vector2(0, 0), 0.01, 0.001));
    }

    @Override
    public void onUse(Player user) {
        double now = getPhysicsTime();
        if (now - lastShootTime > 1 / 12d) {
            lastShootTime = now;
            Vector2 pos = getWorldPoint(new Vector2(0.4, 0.15 * (isFlipped() ? 1 : -1)));
            Vector2 vel = Vector2.create(60, getTransform().getRotationAngle());
            SoundSystem.getInstance().getGameSourceGroup().setLocation(pos.x, pos.y, 0);
            SoundSystem.getInstance().getGameSourceGroup().setVelocity(vel.x * 2, vel.y * 2, 0);
            SoundSystem.getInstance().getGameSourceGroup().play("sounds/weapon/smg-0.wav");

            if (isHostSide()) {
                GameObjectLiteBullet bullet = new GameObjectLiteBullet(pos, vel);
                serverSideWorldSyncer.syncAddObject(bullet);
                serverSideWorldSyncer.syncAddCameraShake(8, getTransform().getRotationAngle() + (Math.random() - 0.5) * Math.PI, 120, true);
                user.body.applyImpulse(Vector2.create(-2, getTransform().getRotationAngle() + (Math.random() - 0.5) * 0.2));
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
        GameObjectItemSMG smg = new GameObjectItemSMG(posX, posY, flipped);
        smg.getTransform().setTranslation(posX, posY);
        smg.getTransform().setRotation(posA);
        smg.setLinearVelocity(velX, velY);
        smg.setAngularVelocity(velA);
        return smg;
    }

    @Override
    public Package createSyncPackage() {
        Package pkg = super.createSyncPackage();
        pkg.setBoolean("flip", isFlipped());
        return pkg;
    }

    @Override
    public void syncFromPackage(Package pkg) {
        super.syncFromPackage(pkg);
        boolean flipped = pkg.getBoolean("flip");
        if (flipped != this.isFlipped()) {
            setFlipped(flipped);
            forceUpdateModel(flipped);
        }
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
