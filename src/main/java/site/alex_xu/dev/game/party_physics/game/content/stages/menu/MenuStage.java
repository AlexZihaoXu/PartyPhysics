package site.alex_xu.dev.game.party_physics.game.content.stages.menu;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.content.GameSettings;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.content.ui.OptionsPane;
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

public class MenuStage extends Stage {
    SoundPlayerGroup group = new SoundPlayerGroup();

    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnPlay = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("PLAY");
    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnOptions = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("OPTIONS");
    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnTutorials = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("TUTORIAL");
    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnExit = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("EXIT");
    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnBack = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("< back");

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

    SoundPlayerSyncer backgroundMusicSyncer;

    OptionsPane optionsPane = new OptionsPane();

    @Override
    public void onLoad() {
        super.onLoad();
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

        optionsPane.setPos(menuShift + xOffset + getWidth() * 1.01 + 260, getHeight() / 2d - 100);
        optionsPane.onRender(renderer);
    }

    @Override
    public void onTick() {
        super.onTick();

        backgroundMusicSyncer.sync();
        optionsPane.onTick();

        masterVolume = GameSettings.getInstance().volumeMaster;
        SoundManager.getInstance().getUIPlayerGroup().setVolume(masterVolume * GameSettings.getInstance().volumeUI);

        if (GameSettings.getInstance().antiAliasingLevel == -1) {
            getWindow().setAutoSwitchAALevelEnabled(true);
        } else {
            getWindow().setAutoSwitchAALevelEnabled(false);
            getWindow().setAALevel(GameSettings.getInstance().antiAliasingLevel);
        }

        muffleShiftTarget = getWindow().getJFrame().isActive() ? 0 : 1;
        if (getWindow().getJFrame().isActive()) {
            backgroundMusicSyncer.syncing = bgmPurePlayer;
            backgroundMusicSyncer.synced = bgmMuffledPlayer;
        } else {
            backgroundMusicSyncer.syncing = bgmMuffledPlayer;
            backgroundMusicSyncer.synced = bgmPurePlayer;
        }

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
        double bgmVolume = GameSettings.getInstance().volumeBackgroundMusic;
        bgmMuffledPlayer.setVolume((muffleShift) * masterVolume * bgmVolume);
        bgmPurePlayer.setVolume((1 - muffleShift) * masterVolume * bgmVolume);

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

    }

    @Override
    public void onMousePressed(double x, double y, int button) {
        super.onMousePressed(x, y, button);
        optionsPane.onMousePressed(x, y, button);
        if (button == 1) {

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
        }
    }

    @Override
    public void onMouseReleased(double x, double y, int button) {
        super.onMouseReleased(x, y, button);
        optionsPane.onMouseReleased(x, y, button);
    }
}
