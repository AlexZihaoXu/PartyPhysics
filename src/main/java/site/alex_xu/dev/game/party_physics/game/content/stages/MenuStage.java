package site.alex_xu.dev.game.party_physics.game.content.stages;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundManager;
import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundPlayer;
import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundPlayerGroup;
import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundPlayerSyncer;
import site.alex_xu.dev.game.party_physics.game.graphics.Font;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

class Button {
    static SoundPlayerGroup group;
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
        group.play(SoundManager.getInstance().get("sounds/ui/menu-btn-over.wav"));
    }

    void onClick() {
        group.play(SoundManager.getInstance().get("sounds/ui/menu-btn-click.wav"));
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

public class MenuStage extends Stage {
    SoundPlayerGroup group = new SoundPlayerGroup();

    Button btnPlay = new Button("PLAY");
    Button btnOptions = new Button("OPTIONS");
    Button btnTutorials = new Button("TUTORIAL");
    Button btnExit = new Button("EXIT");
    Button btnBack = new Button("< back");

    GameWorld world = new GameWorld();
    Camera camera = new Camera();

    Player player;

    double xOffset = 0;

    SoundPlayer bgmMuffledPlayer = new SoundPlayer();
    SoundPlayer bgmPurePlayer = new SoundPlayer();

    double muffleShift = 1;
    double muffleShiftTarget = 0;

    double menuShift = 0;
    double menuShiftProgress = 0;

    double masterVolume = 1;

    boolean atOptions = false;

    Rectangle2D.Double volumeBarBounds = new Rectangle2D.Double();

    boolean adjustingVolumeBar = false;
    boolean mouseOverVolumeBar = false;

    SoundPlayerSyncer backgroundMusicSyncer;

    @Override
    public void onLoad() {
        super.onLoad();
        Button.group = group;
        world.init();
        world.addObject(new GameObjectGround(-60, 4, 120, 1));
        player = new Player(Color.WHITE, 0, -20, 0);
        world.addPlayer(player);
        bgmMuffledPlayer.setSound(SoundManager.getInstance().get("sounds/bgm-0-muffled.wav"));
        bgmPurePlayer.setSound(SoundManager.getInstance().get("sounds/bgm-0-original.wav"));

        bgmPurePlayer.setVolume(0);
        bgmMuffledPlayer.setVolume(1);

        bgmMuffledPlayer.ready();
        bgmPurePlayer.ready();

        bgmMuffledPlayer.play();
        bgmPurePlayer.play();

        backgroundMusicSyncer = new SoundPlayerSyncer(bgmMuffledPlayer, bgmPurePlayer);
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);

        renderer.pushState();

        renderer.setFont(Font.get("fonts/bulkypix.ttf"));

        renderer.setColor(new Color(211, 196, 172));
        renderer.clear();
        renderBackground(renderer);
        renderer.setColor(new Color(211, 196, 172, 120));
        renderer.clear();
        renderButtons(renderer);
        renderForeGround(renderer);
//
//        getWindow().setAutoSwitchAALevelEnabled(false);
//        getWindow().setAALevel(0);

        renderer.popState();

    }

    public void renderBackground(Renderer renderer) {
        camera.render(world, renderer);
    }

    public void renderForeGround(Renderer renderer) {
        renderer.setColor(0);
        renderer.setTextSize(65);

        String title = "Party Physics!";
        renderer.pushState();
        renderer.translate(getWidth() * 0.03 + renderer.getTextWidth(title) / 2 + xOffset, getHeight() * 0.1 + renderer.getTextHeight() / 2);
        renderer.rotate(-0.05);

        renderer.setColor(new Color(98, 92, 85));
        renderer.text(title, -renderer.getTextWidth(title) / 2 + 3, -renderer.getTextHeight() / 2 + 3);
        renderer.setColor(new Color(38, 34, 25));
        renderer.text(title, -renderer.getTextWidth(title) / 2 + 0, -renderer.getTextHeight() / 2 + 0);

        renderer.setColor(new Color(94, 82, 57));
        renderer.translate(renderer.getTextWidth(title) - 450, 70);
        renderer.setTextSize(20);
        renderer.scale(1 + Math.sin(getCurrentTime() * 4) * 0.01);
        String subTitle = "Made by Alex";
        renderer.text(subTitle, -renderer.getTextWidth(subTitle) / 2, -renderer.getTextHeight() / 2);
        renderer.popState();


        renderer.pushState();
        renderer.setColor(new Color(98, 92, 85));
        String version = PartyPhysicsGame.VERSION_STRING;
        renderer.setTextSize(15);
        renderer.text("Version: " + version, 10, getHeight() - renderer.getTextHeight() - 10);

        String copyRight = "(C) 2023 alex-xu.site - All Rights Reserved.";
        renderer.text(copyRight, getWidth() - renderer.getTextWidth(copyRight) - 10, getHeight() - renderer.getTextHeight() - 10);

        renderer.popState();
    }

    public void renderButtons(Renderer renderer) {
        btnPlay.onRender(renderer);
        btnTutorials.onRender(renderer);
        btnOptions.onRender(renderer);
        btnExit.onRender(renderer);

        btnBack.onRender(renderer);

        Vector2 pos = new Vector2(menuShift + xOffset + getWidth() * 1.01 + 260, getHeight() / 2d - 100);
        renderer.pushState();
        renderer.setTextSize(28);
        renderer.setColor(new Color(98, 92, 85));
        renderer.text("OPTIONS", pos.x + 2, pos.y + 2);
        renderer.setColor(new Color(38, 34, 25));
        renderer.text("OPTIONS", pos.x, pos.y);

        renderer.setTextSize(17);
        pos.x += 25;
        pos.y += 50;
        renderer.setColor(new Color(98, 92, 85));
        renderer.text(String.format("Background Music: %2d%%", (int) (masterVolume * 100)), pos.x + 2, pos.y + 2);
        renderer.setColor(new Color(38, 34, 25));
        renderer.text(String.format("Background Music: %2d%%", (int) (masterVolume * 100)), pos.x, pos.y);

        double length = 250;
        renderer.setColor(new Color(98, 92, 85));
        renderer.setLineWidth(1.5);
        pos.y += 25;
        pos.x += 4;

        renderer.setColor(new Color(98, 92, 85));
        renderer.rect(pos.x + 2, pos.y + 2, length, 10);
        renderer.setColor(new Color(138, 132, 127));
        renderer.rect(pos.x, pos.y, length, 10);
        if (mouseOverVolumeBar || adjustingVolumeBar) {
            renderer.setColor(new Color(154, 134, 121));
        } else {
            renderer.setColor(new Color(140, 125, 113));
        }
        renderer.rect(pos.x, pos.y, length * masterVolume, 10);

        renderer.setColor(new Color(98, 92, 85));
        renderer.rect(pos.x + (length * masterVolume - 2) + 2, pos.y - 2, 4, 18);
        if (mouseOverVolumeBar || adjustingVolumeBar) {
            renderer.setColor(new Color(152, 118, 93));
        } else {
            renderer.setColor(new Color(128, 107, 91));
        }
        renderer.rect(pos.x + (length * masterVolume - 2), pos.y - 4, 4, 18);

        volumeBarBounds = new Rectangle2D.Double(pos.x, pos.y-4, length, 18);

        renderer.popState();
    }

    @Override
    public void onTick() {
        super.onTick();

        backgroundMusicSyncer.sync();

        muffleShiftTarget = getWindow().getJFrame().isActive() ? 0 : 1;

        btnPlay.setPos(menuShift + xOffset + getWidth() * 0.01 + 40, getHeight() / 2d - 60);
        btnTutorials.setPos(menuShift + xOffset + getWidth() * 0.01 + 40, getHeight() / 2d);
        btnOptions.setPos(menuShift + xOffset + getWidth() * 0.01 + 40, getHeight() / 2d + 60);
        btnExit.setPos(menuShift + xOffset + getWidth() * 0.01 + 40, getHeight() / 2d + 120);

        btnBack.setPos(menuShift + xOffset + getWidth() * 1.01 + 40, getHeight() / 2d - 80);

        btnPlay.onTick(getDeltaTime(), this);
        btnTutorials.onTick(getDeltaTime(), this);
        btnOptions.onTick(getDeltaTime(), this);
        btnExit.onTick(getDeltaTime(), this);

        btnBack.onTick(getDeltaTime(), this);

        world.onTick();
        camera.scale += (Math.min(getWidth(), getHeight()) / 13d - camera.scale) * Math.min(1, getDeltaTime() * 3);
        camera.pos.x = -menuShift * 0.003;

        if (Math.abs(player.getPos().x - camera.getWorldMousePos().x) > 3) {
            if (player.getPos().x > camera.getWorldMousePos().x) {
                player.setMovementX(-1);
            } else {
                player.setMovementX(1);
            }
        } else {
            player.setMovementX(0);
        }
        if (player.getPos().distance(camera.getWorldMousePos()) < 1) {
            player.setReachDirection(new Vector2());
        } else {
            Vector2 direction = player.getPos().subtract(camera.getWorldMousePos());
            player.setReachDirection(Vector2.create(-1, direction.getDirection()));
        }

        if (getWidth() > 1200) {
            xOffset += ((getWidth() - 1200) / 2d - xOffset) * Math.min(1, getDeltaTime() * 10);
        } else {
            xOffset -= xOffset * Math.min(1, getDeltaTime() * 10);
        }

        muffleShift += (muffleShiftTarget - muffleShift) * Math.min(1, getDeltaTime() * 1.5);
        bgmMuffledPlayer.setVolume((muffleShift) * masterVolume);
        bgmPurePlayer.setVolume((1 - muffleShift) * masterVolume);

        if (bgmPurePlayer.isFinished()) {
            bgmMuffledPlayer.play();
            bgmPurePlayer.play();
        }

        if (atOptions) {
            menuShiftProgress += getDeltaTime();
        } else {
            menuShiftProgress -= getDeltaTime();
        }
        menuShiftProgress = Math.min(1, Math.max(0, menuShiftProgress));
        {
            double n = Math.sin(menuShiftProgress * Math.PI / 2);
            menuShift = getWidth() * (-(n * n * n * n));
        }

        if (adjustingVolumeBar) {
            masterVolume = Math.max(0, Math.min(1, (getMouseX() - volumeBarBounds.getX()) / volumeBarBounds.getWidth()));
        }
        if (volumeBarBounds.contains(getMouseX(), getMouseY())) {
            if (!mouseOverVolumeBar){
                mouseOverVolumeBar = true;
                group.play(SoundManager.getInstance().get("sounds/ui/menu-btn-over.wav"));
            }
        } else {
            mouseOverVolumeBar = false;
        }
    }

    @Override
    public void onMousePressed(double x, double y, int button) {
        super.onMousePressed(x, y, button);
        if (atOptions) {
            if (btnBack.getBounds().contains(x, y)) {
                btnBack.onClick();
                atOptions = false;
            }
        } else {
            if (btnExit.getBounds().contains(x, y)) {
                btnExit.onClick();
                int result = JOptionPane.showConfirmDialog(getWindow().getJFrame(), "Are you sure you want to exit?", "Exit game", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    getWindow().close();
                }
            }
            if (btnOptions.getBounds().contains(x, y)) {
                btnOptions.onClick();
                atOptions = true;
            }

            if (btnPlay.getBounds().contains(x, y)) {
                btnPlay.onClick();
            }
            if (btnTutorials.getBounds().contains(x, y)) {
                btnTutorials.onClick();
            }
        }
        if (volumeBarBounds.contains(x, y)) {
            adjustingVolumeBar = true;
            group.play(SoundManager.getInstance().get("sounds/ui/menu-btn-click.wav"));
        }
    }

    @Override
    public void onMouseReleased(double x, double y, int button) {
        super.onMouseReleased(x, y, button);
        if (adjustingVolumeBar) {
            adjustingVolumeBar = false;
            group.play(SoundManager.getInstance().get("sounds/ui/menu-btn-click.wav"));
        }
    }
}
