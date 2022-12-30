package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class Camera {
    public Vector2 pos = new Vector2();
    public double scale = 1.0;

    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    public double getWidth() {
        return getWindow().getWidth();
    }

    public double getHeight() {
        return getWindow().getHeight();
    }

    public void render(GameWorld world, Renderer renderer) {
        renderer.pushState();
        applyTransform(renderer);
        world.onRender(renderer);
        renderer.popState();
    }

    public void applyTransform(Renderer renderer) {
        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.scale(scale);
        renderer.translate(-pos.x, -pos.y);
    }

    public Vector2 getWorldMousePos() {
        Vector2 mouse = getWindow().getMousePos();
        return new Vector2(
                (mouse.x - getWidth() / 2) / scale + pos.x,
                (mouse.y - getHeight() / 2) / scale + pos.y
        );
    }

}
