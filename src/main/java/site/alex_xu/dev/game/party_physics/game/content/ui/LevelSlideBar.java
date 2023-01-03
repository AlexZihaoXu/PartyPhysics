package site.alex_xu.dev.game.party_physics.game.content.ui;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundManager;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class LevelSlideBar {
    Vector2 pos = new Vector2();
    Rectangle2D bounds = new Rectangle2D.Double();
    double width;
    double height = 8;

    boolean inUse = false;

    int selectedLevel = 0;

    boolean mouseOver = false;

    int levelCount;

    public LevelSlideBar(int levelCount, double width) {
        this.width = width;
        this.levelCount = levelCount;
    }

    public void onRender(Renderer renderer) {
        renderer.pushState();

        renderer.setColor(95, 90, 82);
        renderer.rect(pos.x + 2, pos.y + 2, width, height);

        if (isMouseOver()) {
            renderer.setColor(175, 163, 149);
        } else {
            renderer.setColor(169, 158, 147);
        }
        renderer.rect(pos.x, pos.y, width, height);

        bounds.setRect(pos.x, pos.y - 4, width, height + 8);

        renderer.setColor(134, 125, 110);
        for (int i = 0; i < levelCount; i++) {
            renderer.rect(pos.x + (width - 4) / (levelCount - 1) * i, pos.y, 4, height);
        }
        renderer.setColor(95, 90, 82);
        renderer.rect(pos.x + (width - 4) / (levelCount - 1) * selectedLevel + 2, pos.y - 2, 4, height + 8);
        if (isMouseOver()) {
            renderer.setColor(147, 120, 91);
        } else {
            renderer.setColor(128, 107, 91);
        }
        renderer.rect(pos.x + (width - 4) / (levelCount - 1) * selectedLevel, pos.y - 4, 4, height + 8);


        renderer.popState();
    }

    public void onTick() {
        Vector2 mouse = PartyPhysicsWindow.getInstance().getMousePos();
        if (!PartyPhysicsWindow.getInstance().getMouseButton(1)) {
            if (getBounds().contains(mouse.x, mouse.y)) {
                if (!mouseOver) {
                    this.onMouseOver();
                    mouseOver = true;
                }
            } else {
                mouseOver = false;
            }
        }
        if (inUse) {
            int sectionWidth = (int) (width / (levelCount - 1));
            selectedLevel = (int) ((mouse.x - pos.x + sectionWidth / 2) / sectionWidth);
            selectedLevel = Math.max(0, Math.min(levelCount - 1, selectedLevel));
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
