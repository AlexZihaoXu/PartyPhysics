package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public abstract class GameObject extends Body {

    GameWorld world = null;
    private Vector2 renderPosition = new Vector2();
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
