package site.alex_xu.dev.game.party_physics.game.content.stages;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

/**
 * An abstract class for multiplayer stage that provides some easy draw methods
 */
public class MultiplayerStage extends Stage {

    /**
     * Renders text with field title style
     * @param renderer the renderer to render
     * @param title title
     * @param pos pos
     */
    protected void drawFieldTitle(Renderer renderer, String title, Vector2 pos) {
        drawFieldTitle(renderer, title, pos.x, pos.y);
    }

    /**
     * Renders text with field text style
     * @param renderer the renderer to render
     * @param pos pos
     */
    protected void drawFieldText(Renderer renderer, String text, Vector2 pos) {
        drawFieldText(renderer, text, pos.x, pos.y);
    }

    /**
     * Renders text with field info style
     * @param renderer the renderer to render
     * @param pos pos
     */
    protected void drawFieldInfo(Renderer renderer, String text, Vector2 pos) {
        drawFieldInfo(renderer, text, pos.x, pos.y);
    }

    /**
     * Renders text with field title style
     * @param renderer the renderer to render
     * @param title title
     * @param x x-position
     * @param y y-position
     */
    protected void drawFieldTitle(Renderer renderer, String title, double x, double y) {
        renderer.setFont("fonts/bulkypix.ttf");
        renderer.setTextSize(20);
        renderer.setColor(128, 120, 108);
        renderer.text(title, x + 2, y + 2);
        renderer.setColor(49, 44, 34);
        renderer.text(title, x, y);
    }

    /**
     * Renders text with field text style
     * @param renderer the renderer to render
     * @param x x-position
     * @param y y-position
     */
    protected void drawFieldText(Renderer renderer, String text, double x, double y) {
        renderer.setFont("fonts/bulkypix.ttf");
        renderer.setTextSize(16);
        renderer.setColor(159, 149, 133);
        renderer.text(text, x + 2, y + 2);
        renderer.setColor(73, 66, 51);
        renderer.text(text, x, y);
    }

    /**
     * Renders text with field info style
     * @param renderer the renderer to render
     * @param x x-position
     * @param y y-position
     */
    protected void drawFieldInfo(Renderer renderer, String text, double x, double y) {
        renderer.setFont("fonts/bulkypix.ttf");
        renderer.setTextSize(14);
        renderer.setColor(new Color(168, 158, 141));
        renderer.text(text, x + 2, y + 2);
        renderer.setColor(new Color(101, 93, 72));
        renderer.text(text, x, y);
    }
}
