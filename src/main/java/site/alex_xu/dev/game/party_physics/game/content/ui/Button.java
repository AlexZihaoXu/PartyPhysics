package site.alex_xu.dev.game.party_physics.game.content.ui;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundManager;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

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

    public void setPos(Vector2 pos) {
        this.pos = pos;
    }

    public void setPos(double x, double y) {
        pos.set(x, y);
    }

    public double getX() {
        return pos.x;
    }

    public double getY() {
        return pos.y;
    }

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

    private void onMouseOver() {
        SoundManager.getInstance().getUIPlayerGroup().play(SoundManager.getInstance().get("sounds/ui/mouse-over-0.wav"));
    }

    public void onClick() {
        SoundManager.getInstance().getUIPlayerGroup().play(SoundManager.getInstance().get("sounds/ui/mouse-click-0.wav"));
    }

    public void onRender(Renderer renderer) {
        renderer.pushState();

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
