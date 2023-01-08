package site.alex_xu.dev.game.party_physics.game.content.objects.projectile;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectProjectile;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;

public class GameObjectLiteBullet extends GameObjectProjectile {

    private static final double width = 0.3;
    private static final double height = 0.05;

    private int hitCount = 0;
    private double fadeOutStartTime = 0;

    private double physicsTickTime = 0;


    public GameObjectLiteBullet(Vector2 pos, Vector2 vel) {
        super();
        Rectangle rectangle = new Rectangle(width, height);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFriction(0);
        fixture.setRestitution(0.2);
        setAngularDamping(0);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.003, 0.001));
        setRenderRotationAngle(vel.getDirection());
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
    public void onPhysicsTick(double dt) {
        super.onPhysicsTick(dt);
        physicsTickTime += dt;
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

    private double getTransparency() {
        if (hitCount < 3) {
            return 1;
        }
        double v = Math.min(1, (Clock.currentTime() - fadeOutStartTime) * 1.5);
        return 1 - v * v * v;
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        renderer.pushState();

        int alpha = (int) (getTransparency() * 255);
        renderer.translate(getRenderPos());
        renderer.scale(getTransparency());
        renderer.rotate(getRenderRotationAngle());
        renderer.setColor(new Color(255, 247, 91, alpha));
        renderer.rect(-width / 2, -height / 2, width, height);
        renderer.scale(getTransparency() * 2.4, getTransparency() * 1.5);
        if (hitCount == 0 && physicsTickTime > 0.008) {
            for (int i = 0; i < 3; i++) {
                renderer.setColor(new Color(255, 255, 0, (int) (alpha * 0.5)));
                renderer.rect(-width / 2, -height / 2, width, height, 4);
                renderer.scale(0.8);
            }
        }
        renderer.popState();
    }

    @Override
    public void onHit(GameObject object, Vector2 location) {
        if (object instanceof GameObjectPlayerPart && hitCount == 0) {
            object.applyImpulse(Vector2.create(4, getTransform().getRotationAngle()), location);
        }
        hitCount++;
        if (hitCount == 1) {
            SoundSystem.getInstance().getGameSourceGroup2().setVelocity(0, 0, 0);
            SoundSystem.getInstance().getGameSourceGroup2().setLocation(location.x, location. y, 0);
            SoundSystem.getInstance().getGameSourceGroup2().play("sounds/weapon/hit-0.wav");
        } else if (hitCount == 3) {
            fadeOutStartTime = Clock.currentTime();
        }

    }

    @Override
    public boolean shouldDelete() {
        return getTransparency() <= 0.01;
    }
}
