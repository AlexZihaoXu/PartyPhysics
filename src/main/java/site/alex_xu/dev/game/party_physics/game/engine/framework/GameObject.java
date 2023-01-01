package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public abstract class GameObject extends Body {

    public static int objectIDCounter = 0;
    private final int objectID = objectIDCounter++;

    public int getObjectID() {
        return objectID;
    }

    public Package createSyncPackage() {
        Package pkg = new Package(PackageTypes.PHYSICS_SYNC_GAME_OBJECT_TRANSFORM);

        pkg.setInteger("id", objectID);
        pkg.setFraction("angle", getTransform().getRotationAngle());
        pkg.setFraction("pos.x", getTransform().getTranslationX());
        pkg.setFraction("pos.y", getTransform().getTranslationY());
        pkg.setFraction("vel.x", getLinearVelocity().x);
        pkg.setFraction("vel.y", getLinearVelocity().y);
        pkg.setFraction("vel.a", getAngularVelocity());

        return pkg;
    }

    public void syncFromPackage(Package pkg) {
        double angle = pkg.getFraction("angle");
        double x = pkg.getFraction("pos.x");
        double y = pkg.getFraction("pos.y");
        double vx = pkg.getFraction("vel.x");
        double vy = pkg.getFraction("vel.y");
        double va = pkg.getFraction("vel.a");

        getTransform().setRotation(angle);
        getTransform().setTranslation(x, y);
        setLinearVelocity(vx, vy);
        setAngularVelocity(va);
    }

    public Package createCreationPackage() {
        Package pkg = new Package(PackageTypes.PHYSICS_SYNC_GAME_OBJECT_CREATE);
        pkg.setInteger("id", getObjectID());
        pkg.setFraction("pos.x", getTransform().getTranslationX());
        pkg.setFraction("pos.y", getTransform().getTranslationY());
        pkg.setFraction("pos.a", getTransform().getRotationAngle());
        pkg.setFraction("vel.x", getLinearVelocity().x);
        pkg.setFraction("vel.y", getLinearVelocity().y);
        pkg.setFraction("vel.a", getAngularVelocity());
        return pkg;
    }

    public abstract GameObject createFromPackage(Package pkg);

    GameWorld world = null;
    private final Vector2 renderPosition = new Vector2();
    private double renderRotationAngle = 0;

    public Vector2 getRenderPos() {
        return renderPosition;
    }

    public double getRenderRotationAngle() {
        return renderRotationAngle;
    }

    public void onTickAnimation() {
        Vector2 vel = getLinearVelocity().copy();
        double velLimit = 3;
        double rotLimit = Math.PI / 4;
        renderRotationAngle += getDeltaTime() * Math.min(rotLimit, Math.max(-rotLimit, getAngularVelocity()));
        vel.x = Math.min(velLimit, Math.max(-velLimit, vel.x));
        vel.y = Math.min(velLimit, Math.max(-velLimit, vel.y));
        renderPosition.x += vel.x * getDeltaTime();
        renderPosition.y += vel.y * getDeltaTime();
    }

    public double getDeltaTime() {
        return getWindow().getDeltaTime();
    }

    public double getCurrentTime() {
        return getWindow().getCurrentTime();
    }

    public GameWorld getWorld() {
        return world;
    }

    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    public void onTick() {

    }

    public void onPhysicsTick(double dt) {
        renderPosition.set(getTransform().getTranslation());
        renderRotationAngle = getTransform().getRotationAngle();
    }

    abstract public void onRender(Renderer renderer);
}
