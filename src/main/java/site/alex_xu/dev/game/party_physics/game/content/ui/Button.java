package site.alex_xu.dev.game.party_physics.game.content.ui;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Font;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Button class
 * Handles button rendering and click sounds
 */
public class Button {
    private final String title;
    private Vector2 pos = new Vector2();

    private boolean hovered = false;
    private final double width = 220;
    private final double height = 50;

    private double animationProgress = 0;
    private double animationRate = 0;

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(getX(), getY(), width, height);
    }

    public Button(String title) {
        this.title = title;

    }

    /**
     * @param pos the position of the button
     */
    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    /**
     * @param x x-position of the button
     * @param y y-position of the button
     */
    public void setPos(double x, double y) {
        pos.set(x, y);
    }

    /**
     * @return x-position of the button
     */
    public double getX() {
        return pos.x;
    }

    /**
     * @return y-position of the button
     */
    public double getY() {
        return pos.y;
    }

    /**
     * @param dt delta time between last tick and current tick
     * @param stage the stage
     */
    public void onTick(double dt, Stage stage) {
        Vector2 pos = stage.getMousePos();
        if (getBounds().contains(pos.x, pos.y)) {
            animationProgress += dt * 5;
            if (!hovered) {
                hovered = true;
                this.onMouseOver();
            }
        } else {
            hovered = false;
            animationProgress -= dt * 5;
        }
        animationProgress = Math.max(0, Math.min(1, animationProgress));
        double x = 1 - animationProgress;
        animationRate = Math.max(0, Math.min(1, 1 + (x * x * x * (x - 2))));
    }

    /**
     * Gets called when mouse is over (players mouse over sound)
     */
    private void onMouseOver() {
        SoundSystem.getInstance().getUISourceGroup().play("sounds/ui/mouse-over-0.wav");
    }

    /**
     * Gets called when mouse clicks (plays click sound)
     */
    public void onClick() {
        SoundSystem.getInstance().getUISourceGroup().play("sounds/ui/mouse-click-0.wav");
    }

    /**
     * Gets called when it should be rendered
     * Draws the button using the given renderer
     * @param renderer renderer to render
     */
    public void onRender(Renderer renderer) {
        renderer.pushState();

        renderer.setFont(Font.get("fonts/bulkypix.ttf"));

        renderer.setColor(210, 195, 171, (int) (animationRate * 150));
        renderer.rect(getX(), getY(), width, height);
        renderer.setTextSize(28);

        double posOffset = animationRate * 1.2;
        int colorOffset = (int) (animationRate * 40);

        renderer.setColor(new Color(138, 132, 127));
        renderer.text(title, getX() + 16 + posOffset, getY() + 12 + posOffset);
        renderer.setColor(98 - colorOffset, 92 - colorOffset, 85 - colorOffset);
        renderer.text(title, getX() + 16 - posOffset, getY() + 12 - posOffset);

        if (animationRate > 0.01) {
            double length = animationRate * (width - 30);
            renderer.setColor(new Color(138, 132, 127));
            renderer.line(getX() + 15, getY() + height - 6, getX() + length, getY() + height - 6);
        }

        renderer.popState();
    }
}
