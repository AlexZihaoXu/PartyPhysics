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
import site.alex_xu.dev.game.party_physics.game.engine.sounds.Sound;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSource;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Font;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MenuStage extends Stage {

    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnPlay = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("PLAY");
    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnOptions = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("OPTIONS");
    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnTutorials = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("TUTORIAL");
    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnExit = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("EXIT");
    site.alex_xu.dev.game.party_physics.game.content.ui.Button btnBack = new site.alex_xu.dev.game.party_physics.game.content.ui.Button("< back");

    GameWorld world = new GameWorld();
    Camera camera = new Camera();

    Player player;

    double zoomProgress = 0.01;

    double xOffset = 0;

    double muffleShift = 1;

    double menuShift = 0;
    double menuShiftProgress = 0;

    double masterVolume = 1;

    boolean atSecondPage = false;
    boolean secondPageAtOptions = false;

    OptionsPane optionsPane = new OptionsPane();

    PlayPage playPage = new PlayPage(this);

    SoundSource bgm = new SoundSource();

    Clock timer = new Clock();

    @Override
    public void onLoad() {
        super.onLoad();
        Sound sound = Sound.get("sounds/bgm-0.wav");
        bgm.setSound(sound);
        bgm.setVolume(1);

        bgm.play();

        world.init();
        world.addObject(new GameObjectGround(-60, 4, 120, 1));
    }

    @Override
    public void onOffload() {
        super.onOffload();
        bgm.delete();
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);

        renderer.pushState();

        renderer.translate(getWidth() / 2, getHeight() / 2);
        double zoom;
        {
            zoomProgress += Math.min(0.1, getDeltaTime());
            zoomProgress = Math.min(1, zoomProgress);

            double x = 1 - zoomProgress;
            zoom = 1 - x * x * x;

        }
        renderer.scale(zoom);
        renderer.translate(-getWidth() / 2, -getHeight() / 2);

        renderer.setFont(Font.get("fonts/bulkypix.ttf"));

        renderer.setColor(new Color(211, 196, 172));
        renderer.clear();
        renderBackground(renderer);
        renderer.setColor(new Color(211, 196, 172, 120));
        renderer.clear();
        renderUIComponents(renderer);
        renderForeGround(renderer);
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

    public void renderUIComponents(Renderer renderer) {
        btnPlay.onRender(renderer);
        btnTutorials.onRender(renderer);
        btnOptions.onRender(renderer);
        btnExit.onRender(renderer);

        btnBack.onRender(renderer);

        optionsPane.setPos(menuShift + xOffset + getWidth() * 1.01 + 260, getHeight() / 2d - 100 - getHeight() * (secondPageAtOptions ? 0 : 1));
        optionsPane.onRender(renderer);

        double x1 = menuShift + xOffset + getWidth() * 1.01 + 260;
        double x2 = menuShift + getWidth() * 1.5 - 200;
        playPage.setPos(Math.max(x1, x2), getHeight() / 2d - 100 - getHeight() * (secondPageAtOptions ? 1 : 0));
        playPage.onRender(renderer);

    }

    @Override
    public void onMouseMove(double x, double y) {
        super.onMouseMove(x, y);

        if (timer.elapsedTime() > 1.5 && player == null) {
            player = new Player(Color.WHITE, 0, -20, 0);
            world.addPlayer(player);
        }
    }

    @Override
    public void onTick() {
        super.onTick();

        optionsPane.onTick();
        playPage.onTick();

        masterVolume = GameSettings.getInstance().volumeMaster;
        SoundSystem.getInstance().getUISourceGroup().setVolume(masterVolume * GameSettings.getInstance().volumeUI);

        if (GameSettings.getInstance().antiAliasingLevel == -1) {
            getWindow().setAutoSwitchAALevelEnabled(true);
        } else {
            getWindow().setAutoSwitchAALevelEnabled(false);
            getWindow().setAALevel(GameSettings.getInstance().antiAliasingLevel);
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

        if (player != null){
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

        }

        if (getWidth() > 1200) {
            xOffset += ((getWidth() - 1200) / 2d - xOffset) * Math.min(1, getDeltaTime() * 10);
        } else {
            xOffset -= xOffset * Math.min(1, getDeltaTime() * 10);
        }

        double bgmVolume = GameSettings.getInstance().volumeBackgroundMusic;
        bgm.setVolume(bgmVolume);

        if (atSecondPage) {
            menuShiftProgress += getDeltaTime();
        } else {
            menuShiftProgress -= getDeltaTime();
        }
        menuShiftProgress = Math.min(1, Math.max(0, menuShiftProgress));
        {
            double n = Math.sin(menuShiftProgress * Math.PI / 2);
            menuShift = getWidth() * (-(n * n * n * n));
        }

        double muffleShiftTarget;
        if (getWindow().getJFrame().isActive()) {
            if (atSecondPage) {
                muffleShiftTarget = 0.65;
            } else {
                muffleShiftTarget = 0;
            }
        } else {
            muffleShiftTarget = 1;
        }

        muffleShift += (muffleShiftTarget - muffleShift) * Math.min(1, getDeltaTime() * 3);
        bgm.setMufflePercentage(muffleShift);
        double muffle = SoundSystem.getInstance().getMasterMuffle() + getDeltaTime() * (getWindow().getJFrame().isActive() ? -1 : 1);
        muffle = Math.max(0, Math.min(1, muffle));
        SoundSystem.getInstance().setMasterMuffle(muffle);

        if (bgm.isStopped()) {
            bgm.play();
        }

    }

    @Override
    public void onMousePressed(double x, double y, int button) {
        super.onMousePressed(x, y, button);
        optionsPane.onMousePressed(x, y, button);
        if (button == 1) {
            if (atSecondPage) {
                if (btnBack.getBounds().contains(x, y)) {
                    btnBack.onClick();
                    atSecondPage = false;
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
                    atSecondPage = true;
                    secondPageAtOptions = true;
                }
                if (btnPlay.getBounds().contains(x, y)) {
                    btnPlay.onClick();
                    atSecondPage = true;
                    secondPageAtOptions = false;
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

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        if (keyCode == KeyEvent.VK_ESCAPE) {
            atSecondPage = false;
        }
    }
}
