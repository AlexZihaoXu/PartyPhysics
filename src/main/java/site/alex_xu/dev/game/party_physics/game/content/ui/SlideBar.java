package site.alex_xu.dev.game.party_physics.game.content.ui;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundManager;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.geom.Rectangle2D;

public class SlideBar {
    Vector2 pos = new Vector2();
    Rectangle2D bounds = new Rectangle2D.Double();

    double percentage = 1;
    double width;
    double height = 8;

    boolean inUse = false;

    boolean mouseOver = false;

    public SlideBar(double width) {
        this.width = width;
    }

    public void onRender(Renderer renderer) {
        renderer.pushState();

        renderer.setColor(95, 90, 82);
        renderer.rect(pos.x + 2, pos.y + 2, width, height);
        renderer.setColor(138, 132, 127);
        renderer.rect(pos.x, pos.y, width, height);

        if (isMouseOver()) {
            renderer.setColor(155, 135, 119);
        } else {
            renderer.setColor(140, 125, 113);
        }
        renderer.rect(pos.x, pos.y, width * percentage, height);

        renderer.setColor(95, 90, 82);
        renderer.rect(pos.x + width * percentage, pos.y - 2, 4, height + 8);
        if (isMouseOver()) {
            renderer.setColor(147, 120, 91);
        } else {
            renderer.setColor(128, 107, 91);
        }
        renderer.rect(pos.x + width * percentage - 2, pos.y - 4, 4, height + 8);

        bounds.setRect(pos.x, pos.y - 4, width, height + 8);

        renderer.popState();
    }

    public void onTick() {
        Vector2 pos = PartyPhysicsWindow.getInstance().getMousePos();
        if (!PartyPhysicsWindow.getInstance().getMouseButton(1)) {
            if (getBounds().contains(pos.x, pos.y)) {
                if (!mouseOver) {
                    this.onMouseOver();
                    mouseOver = true;
                }
            } else {
                mouseOver = false;
            }
        }
        if (inUse) {
            percentage = Math.min(1, Math.max(0, (pos.x - this.pos.x) / width ));
        }
    }

    public void onMousePress(double x, double y, int button) {
        if (button == 1) {
            if (getBounds().contains(x, y)) {
                inUse = true;
                SoundManager.getInstance().getUIPlayerGroup().play("sounds/ui/mouse-click-0.wav");
            }
        }
    }

    public void onMouseRelease(double x, double y, int button) {
        if (button == 1) {
            if (inUse) {
                inUse = false;
                SoundManager.getInstance().getUIPlayerGroup().play("sounds/ui/mouse-click-0.wav");
            }
        }
    }

    private void onMouseOver() {
        SoundManager.getInstance().getUIPlayerGroup().play("sounds/ui/mouse-over-0.wav");
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }
}
