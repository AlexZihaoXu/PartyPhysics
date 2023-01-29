package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync.ServerSideWorldSyncer;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

/**
 * The parent class of all game objects
 * All classes extended from this should be able to simulate and render
 */
public abstract class GameObject extends Body {

    public static int nextObjectID = 0;
    private final int objectID = nextObjectID++;

    public static double latency = 0;

    /**
     * @return the color of the particle when hit by bullet
     */
    public abstract Color getHitParticleColor();

    public static ServerSideWorldSyncer serverSideWorldSyncer = null;

    /**
     * @return true if the simulation is on host side
     */
    public static boolean isHostSide() {
        return serverSideWorldSyncer != null;
    }

    /**
     * @return the unique ID of the current object
     */
    public int getObjectID() {
        return objectID;
    }

    /**
     * @return a package that helps synchronize states of this object
     */
    public Package createSyncPackage() {
        Package pkg = new Package(PackageTypes.WORLD_SYNC_OBJECT_STATE);

        pkg.setInteger("id", objectID);
        pkg.setFraction("angle", getTransform().getRotationAngle());
        pkg.setFraction("pos.x", getTransform().getTranslationX());
        pkg.setFraction("pos.y", getTransform().getTranslationY());
        pkg.setFraction("vel.x", getLinearVelocity().x);
        pkg.setFraction("vel.y", getLinearVelocity().y);
        pkg.setFraction("vel.a", getAngularVelocity());

        return pkg;
    }

    /**
     * Synchronize the current object's state based on the given package.
     * @param pkg the package to sync states from
     */
    public void syncFromPackage(Package pkg) {
        double angle = pkg.getFraction("angle");
        double x = pkg.getFraction("pos.x");
        double y = pkg.getFraction("pos.y");
        double vx = pkg.getFraction("vel.x");
        double vy = pkg.getFraction("vel.y");
        double va = pkg.getFraction("vel.a");

        getTransform().setTranslation(x, y);
        getTransform().setRotation(angle);
        setLinearVelocity(vx, vy);
        setAngularVelocity(va);
    }

    /**
     * @return a package that contains information about creating this object
     */
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

    /**
     * @param pkg package that contains creation information of this object
     * @return a new created object based on the given package
     */
    public abstract GameObject createFromPackage(Package pkg);

    GameWorld world = null;
    private final Vector2 renderPosition = new Vector2();
    private double renderRotationAngle = 0;

    /**
     * @return the position on the screen (can be slightly different from the actual simulated position)
     */
    public Vector2 getRenderPos() {
        if (renderPosition.distanceSquared(getTransform().getTranslation()) > 9) {
            renderPosition.set(getTransform().getTranslation());
        }
        return renderPosition;
    }

    /**
     * @return the angle on the screen (can be slightly different from the actual simulated angle)
     */
    public double getRenderRotationAngle() {
        return renderRotationAngle;
    }

    /**
     * @param angle the new render angle
     */
    protected void setRenderRotationAngle(double angle) {
        renderRotationAngle = angle;
    }

    /**
     * Calculate and update render pos and angle based on linear velocity and angular velocity
     * (This allows the game to run at maximum smoothness even if the physics simulation is at a lower TPS)
     */
    public void onTickAnimation() {
        Vector2 vel = getLinearVelocity().copy();
        double velLimit = 0.5;
        double rotLimit = Math.PI / 4;
        renderRotationAngle += getDeltaTime() * Math.min(rotLimit, Math.max(-rotLimit, getAngularVelocity()));
        vel.x = Math.min(velLimit, Math.max(-velLimit, vel.x));
        vel.y = Math.min(velLimit, Math.max(-velLimit, vel.y));
        renderPosition.x += vel.x * getDeltaTime();
        renderPosition.y += vel.y * getDeltaTime();
    }

    /**
     * @return the delta time between last tick and current tick
     */
    public double getDeltaTime() {
        return getWindow().getDeltaTime();
    }

    /**
     * @return current time in seconds
     */
    public double getCurrentTime() {
        return getWindow().getCurrentTime();
    }

    /**
     * @return the world where this object lives in
     */
    public GameWorld getWorld() {
        return world;
    }

    /**
     * @return the window instance of the game
     */
    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    /**
     * Gets called everytime when the game requires to tick
     */
    public void onTick() {

    }

    /**
     * Gets called when the physics engine requires an update of the world
     * @param dt delta time between last tick and current tick
     */
    public void onPhysicsTick(double dt) {
        Vector2 translation = getTransform().getTranslation();
        renderPosition.x += (translation.x - renderPosition.x) * Math.min(1, dt * 67);
        renderPosition.y += (translation.y - renderPosition.y) * Math.min(1, dt * 67);
        renderRotationAngle = getTransform().getRotationAngle();
    }

    /**
     * Gets called when the game requires this object to render in the world
     * @param renderer renderer
     */
    abstract public void onRender(Renderer renderer);
}
