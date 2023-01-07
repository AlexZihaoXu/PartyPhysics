package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

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
        renderPlayerNameTag(world, renderer);
    }

    public void renderPlayerNameTag(GameWorld world, Renderer renderer) {
        renderer.pushState();

        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.setFont("fonts/bulkypix.ttf");

        for (Player player : world.players.values()) {
            String displayName = player.getDisplayName();
            if (displayName != null) {
                renderer.pushState();

                Color color = player.getColor().darker();
                renderer.setColor(color.getRed(), color.getGreen(), color.getBlue(), 225);
                Vector2 pos = player.head.getRenderPos().copy();
                pos.y -= 0.35;
                renderer.translate(
                        (pos.x - this.pos.x) * scale, (pos.y - this.pos.y) * scale
                );

                renderer.triangle(-10, -9, 10, -9, 0, 0);
                renderer.translate(0, -9);

                renderer.text(
                        displayName,
                        -renderer.getTextWidth(displayName) / 2, -renderer.getTextHeight()
                );

                renderer.popState();
            }
        }

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
