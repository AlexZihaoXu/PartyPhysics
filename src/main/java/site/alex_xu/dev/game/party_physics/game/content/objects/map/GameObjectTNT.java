package site.alex_xu.dev.game.party_physics.game.content.objects.map;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.particles.ExplosionSmokeParticle;
import site.alex_xu.dev.game.party_physics.game.content.particles.ExplosionSparkParticle;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerHead;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.util.ArrayList;

/**
 * TNT object class
 * (explosive)
 */
public class GameObjectTNT extends GameObject {

    double size = 0.7;

    boolean exploded = false;

    public GameObjectTNT(double x, double y) {
        super();
        org.dyn4j.geometry.Rectangle rectangle = new Rectangle(size, size * 1.1);
        BodyFixture fixture = new BodyFixture(rectangle);
        fixture.setFriction(0.2);
        addFixture(fixture);
        setMass(new Mass(new Vector2(0, 0), 0.085, 0.3));
        translate(x + size / 2, y + size / 2);
    }


    @Override
    public Color getHitParticleColor() {
        return new Color(236, 38, 74);
    }

    /**
     * Try to ignite this TNT
     */
    public void ignite() {
        if (exploded) {
            return;
        }
        exploded = true;
        spawnParticles();

        if (isHostSide()) {

            serverSideWorldSyncer.syncPlaySound("sounds/tnt.wav", getRenderPos().x, getRenderPos().y);

            ArrayList<Player> damagedPlayers = new ArrayList<>();
            for (GameObject object : getWorld().getObjects()) {
                double distance = object.getTransform().getTranslation().distance(this.getTransform().getTranslation());
                double force = 8 / distance;
                Vector2 normalized = getTransform().getTranslation().copy().subtract(object.getTransform().getTranslation()).getNormalized();
                if (object != this) {
                    if (object instanceof GameObjectPlayerPart) {
                        double dmg = force / 50;
                        if (dmg > 0.01) {
                            Player player = ((GameObjectPlayerPart) object).getPlayer();
                            player.setHealth(player.getHealth() - dmg);
                            damagedPlayers.add(player);
                            normalized.add(0, 0.3);
                        }
                    }
                    object.applyImpulse(normalized.cross(force));
                }
            }

            for (Player player : damagedPlayers) {
                serverSideWorldSyncer.syncPlayerUpdateHP(player);
            }
        }
    }

    /**
     * @return true if this TNT is already exploded
     */
    public boolean isExploded() {
        return exploded;
    }

    /**
     * Spawn explosion particles
     */
    private void spawnParticles() {
        GameWorld world = getWorld();
        double directionCount = Math.round(Math.random() * 3 + 2);
        for (int i = 0; i <= directionCount; i++) {
            double direction = -(i / directionCount) * Math.PI;
            for (int j = 0; j < 12; j++) {
                double rand = Math.random();
                Vector2 vel = Vector2.create(0.4 + (1 - rand * rand) * 3, direction + (Math.random() - 0.5) * 0.3);
                if (Math.random() > 0.6) {
                    ExplosionSmokeParticle particle = new ExplosionSmokeParticle(getRenderPos(), vel, Math.random() * 0.3 + 0.3);
                    vel.product(0.01);
                    rand = Math.random();
                    particle.color = new Color(243, (int) (140 + (rand * rand) * 90), 100);
                    world.addParticle(particle);
                } else {
                    ExplosionSmokeParticle particle = new ExplosionSmokeParticle(getRenderPos(), vel, Math.random() * 1.4 + 0.3);
                    world.addParticle(particle);
                }
            }
        }
        for (int i = 0; i < directionCount * 2; i++) {
            double direction = -(i / directionCount / 2) * Math.PI;
            ExplosionSparkParticle particle = new ExplosionSparkParticle(
                    getRenderPos(),
                    Vector2.create(Math.random() * 5 + 4, direction).add(0, -Math.random() * 4 - 2), world
            );
            world.addParticle(particle);
        }
    }


    @Override
    public Package createCreationPackage() {
        Package pkg = super.createCreationPackage();

        pkg.setBoolean("exploded", exploded);

        return pkg;
    }

    @Override
    public void syncFromPackage(Package pkg) {
        super.syncFromPackage(pkg);
        exploded = pkg.getBoolean("exploded");
    }

    @Override
    public Package createSyncPackage() {
        Package pkg = super.createSyncPackage();
        pkg.setBoolean("exploded", exploded);
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
        boolean exploded = pkg.getBoolean("exploded");

        GameObject.nextObjectID = id;
        GameObjectTNT tnt = new GameObjectTNT(posX - size / 2, posY - size / 2);
        tnt.getTransform().setTranslation(posX, posY);
        tnt.getTransform().setRotation(posA);
        tnt.setLinearVelocity(velX, velY);
        tnt.setAngularVelocity(velA);
        tnt.exploded = exploded;
        return tnt;
    }

    @Override
    public void onRender(Renderer renderer) {

        renderer.pushState();

        renderer.translate(getRenderPos());
        renderer.rotate(getRenderRotationAngle());

        renderer.pushState();

        renderer.setColor(new Color(236, 38, 74));
        renderer.rect(-0.095, -0.4, 0.19, 0.8, 0.1);
        renderer.translate(-0.24, 0);
        renderer.rect(-0.095, -0.4, 0.19, 0.8, 0.1);
        renderer.translate(0.48, 0);
        renderer.rect(-0.095, -0.4, 0.19, 0.8, 0.1);
        renderer.popState();

        renderer.pushState();

        renderer.setColor(68, 58, 56);

        renderer.translate(0, 0.02);
        renderer.rect(-0.38, -0.1 - 0.25, 0.76, 0.15, 0.1);
        renderer.rect(-0.38, -0.1 + 0.25, 0.76, 0.15, 0.1);
        renderer.rect(-0.35, -0.1, 0.7, 0.15, 0.07);

        renderer.setColor(201, 205, 229);
        renderer.rect(-0.25, -0.15, 0.5, 0.26, 0.05);
        renderer.setColor(Color.white);
        renderer.rect(-0.21, -0.11, 0.42, 0.18, 0.05);

        renderer.setColor(Color.black);
        renderer.scale(0.4);
        renderer.translate(-0.32, -0.32);
        renderer.rect(-0.15, 0.1, 0.3, 0.1, 0.03);
        renderer.rect(-0.05, 0.1, 0.1, 0.35, 0.03);

        renderer.translate(0.64, 0);
        renderer.rect(-0.15, 0.1, 0.3, 0.1, 0.03);
        renderer.rect(-0.05, 0.1, 0.1, 0.35, 0.03);
        renderer.translate(-0.32, 0);

        renderer.rect(-0.15, 0.1, 0.1, 0.35, 0.03);
        renderer.rect(0.05, 0.1, 0.1, 0.35, 0.03);

        renderer.translate(-0.15, 0.15);
        renderer.rotate(-0.56);
        renderer.rect(0, 0, 0.1, 0.35, 0.03);

        renderer.popState();

        renderer.popState();
    }
}
