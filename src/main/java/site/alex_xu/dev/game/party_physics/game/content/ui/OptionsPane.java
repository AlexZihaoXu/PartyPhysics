package site.alex_xu.dev.game.party_physics.game.content.ui;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.GameSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class OptionsPane {

    Vector2 pos = new Vector2();
    SlideBar slideBarMasterVolume = new SlideBar(250);
    SlideBar slideBarBackgroundVolume = new SlideBar(250);
    SlideBar slideBarUIVolume = new SlideBar(250);
    LevelSlideBar slideBarAntiAliasing = new LevelSlideBar(4, 250);

    public OptionsPane() {
        slideBarAntiAliasing.selectedLevel = GameSettings.getInstance().antiAliasingLevel + 1;
        slideBarUIVolume.percentage = GameSettings.getInstance().volumeUI;
        slideBarBackgroundVolume.percentage = GameSettings.getInstance().volumeBackgroundMusic;
        slideBarMasterVolume.percentage = GameSettings.getInstance().volumeMaster;
    }

    public void onRender(Renderer renderer) {

        renderer.pushState();
        renderer.setTextSize(28);
        renderer.setColor(new Color(98, 92, 85));
        renderer.text("OPTIONS", pos.x + 2, pos.y + 2);
        renderer.setColor(new Color(38, 34, 25));
        renderer.text("OPTIONS", pos.x, pos.y);

        renderer.setTextSize(17);

        pos.x += 25;

        pos.y += 50;

        {
            double masterVolume = GameSettings.getInstance().volumeMaster;
            renderer.setColor(new Color(98, 92, 85));
            renderer.text(String.format("Master Volume: %2d%%", (int) Math.round(masterVolume * 100)), pos.x + 2, pos.y + 2);
            renderer.setColor(new Color(38, 34, 25));
            renderer.text(String.format("Master Volume: %2d%%", (int) Math.round(masterVolume * 100)), pos.x, pos.y);

            slideBarMasterVolume.pos.set(pos);
            slideBarMasterVolume.pos.y += 30;
            slideBarMasterVolume.pos.x += 5;
            slideBarMasterVolume.onRender(renderer);
        }

        pos.y += 55;
        {
            double backgroundVolume = GameSettings.getInstance().volumeBackgroundMusic;
            renderer.setColor(new Color(98, 92, 85));
            renderer.text(String.format("Background Music: %2d%%", (int) Math.round(backgroundVolume * 100)), pos.x + 2, pos.y + 2);
            renderer.setColor(new Color(38, 34, 25));
            renderer.text(String.format("Background Music: %2d%%", (int) Math.round(backgroundVolume * 100)), pos.x, pos.y);

            slideBarBackgroundVolume.pos.set(pos);
            slideBarBackgroundVolume.pos.y += 30;
            slideBarBackgroundVolume.pos.x += 5;
            slideBarBackgroundVolume.onRender(renderer);
        }
        pos.y += 55;
        {
            double uiVolume = GameSettings.getInstance().volumeUI;
            renderer.setColor(new Color(98, 92, 85));
            renderer.text(String.format("UI Volume: %2d%%", (int) Math.round(uiVolume * 100)), pos.x + 2, pos.y + 2);
            renderer.setColor(new Color(38, 34, 25));
            renderer.text(String.format("UI Volume: %2d%%", (int) Math.round(uiVolume * 100)), pos.x, pos.y);

            slideBarUIVolume.pos.set(pos);
            slideBarUIVolume.pos.y += 30;
            slideBarUIVolume.pos.x += 5;
            slideBarUIVolume.onRender(renderer);
        }


        pos.y += 55;
        {
            String aaLevelString;
            if (GameSettings.getInstance().antiAliasingLevel == -1) {
                int level = PartyPhysicsWindow.getInstance().getAALevel();
                if (level == 0) {
                    aaLevelString = "Auto (off)";
                } else {
                    int value = (int) Math.pow(2, level);
                    aaLevelString = "Auto (X" + (value * value) + ")";
                }
            } else {
                int value = (int) Math.pow(2, PartyPhysicsWindow.getInstance().getAALevel());
                if (value == 1) {
                    aaLevelString = "off";
                } else {
                    aaLevelString = "X" + (value * value);
                }
            }

            renderer.setColor(new Color(98, 92, 85));
            renderer.text("Antialiasing: " + aaLevelString, pos.x + 2, pos.y + 2);
            renderer.setColor(new Color(38, 34, 25));
            renderer.text("Antialiasing: " + aaLevelString, pos.x, pos.y);
            slideBarAntiAliasing.pos.set(pos);
            slideBarAntiAliasing.pos.y += 30;
            slideBarAntiAliasing.pos.x += 5;
            slideBarAntiAliasing.onRender(renderer);

        }

        renderer.popState();
    }

    public void onMouseReleased(double x, double y, int button) {
        slideBarMasterVolume.onMouseRelease(x, y, button);
        slideBarAntiAliasing.onMouseRelease(x, y, button);
        slideBarUIVolume.onMouseRelease(x, y, button);
        slideBarBackgroundVolume.onMouseRelease(x, y, button);
    }

    public void onMousePressed(double x, double y, int button) {
        slideBarMasterVolume.onMousePress(x, y, button);
        slideBarAntiAliasing.onMousePress(x, y, button);
        slideBarBackgroundVolume.onMousePress(x, y, button);
        slideBarUIVolume.onMousePress(x, y, button);
    }

    public void setPos(double x, double y) {
        pos.set(x, y);
    }

    public void onTick() {
        slideBarMasterVolume.onTick();
        GameSettings.getInstance().volumeMaster = slideBarMasterVolume.percentage;

        slideBarAntiAliasing.onTick();
        GameSettings.getInstance().antiAliasingLevel = slideBarAntiAliasing.selectedLevel - 1;

        slideBarUIVolume.onTick();
        GameSettings.getInstance().volumeUI = slideBarUIVolume.percentage;

        slideBarBackgroundVolume.onTick();
        GameSettings.getInstance().volumeBackgroundMusic = slideBarBackgroundVolume.percentage;

    }
}
