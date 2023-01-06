package site.alex_xu.dev.game.party_physics.game.content.stages.join;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.content.stages.MultiplayerStage;
import site.alex_xu.dev.game.party_physics.game.content.stages.menu.MenuStage;
import site.alex_xu.dev.game.party_physics.game.content.ui.Button;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.Client;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.JoiningClient;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSource;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import javax.swing.*;
import java.awt.*;

public class JoinStage extends MultiplayerStage {

    SoundSource bgm;
    JoiningClient client;

    String name;

    public JoinStage(SoundSource bgm, JoiningClient client, String name) {
        this.bgm = bgm;
        this.client = client;
        this.name = name;
    }

    Button btnBack = new Button("< leave");

    private double xOffset = 0;
    private double enterProgress = 0;

    private Camera camera = new Camera();

    @Override
    public void onLoad() {
        super.onLoad();
        if (getWidth() > 1200) {
            xOffset = (getWidth() - 1200) / 2d;
        } else {
            xOffset = 0;
        }

        {
            Package pkg = new Package(PackageTypes.HANDSHAKE);
            pkg.setString("name", name);

            client.send(pkg);
        }
    }

    @Override
    public void onOffload() {
        super.onOffload();

    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);

        {
            enterProgress += Math.min(0.05, getDeltaTime());
            enterProgress = Math.min(1, enterProgress);

            double x = 1 - enterProgress;
            renderer.translate(getWidth() / 2, getHeight() / 2);
            renderer.scale(1 - x * x * x * x * x);
            renderer.translate(-getWidth() / 2, -getHeight() / 2);
        }

        renderer.setColor(210, 195, 171);
        renderer.clear();

        if (client.getSyncedWorld() != null) {
            if (client.getSyncedWorld() != null && client.getSyncedWorld().hasPlayer(client.getOwnClient().getID())) {
                Player player = client.getSyncedWorld().getPlayer(client.getOwnClient().getID());
                camera.scale += (Math.min(getWidth(), getHeight()) / 14d - camera.scale) * Math.min(1, getDeltaTime() * 3);
                camera.pos.x += (player.getPos().x - camera.pos.x) * Math.min(1, getDeltaTime() * 2);
                camera.pos.y += (player.getPos().y - camera.pos.y) * Math.min(1, getDeltaTime());
            } else {
                camera.scale += (45 - camera.scale) * Math.min(1, getDeltaTime() * 5);
            }
            camera.render(client.getSyncedWorld(), renderer);
        }

        btnBack.setPos(50 + xOffset, 50);
        btnBack.onRender(renderer);

        renderUIComponents(renderer);
    }

    public void renderUIComponents(Renderer renderer) {
        String title = client.getHostName() == null ? "Connecting ... " : client.getHostName() + "'s Game";

        renderer.pushState();
        renderer.translate(xOffset + 300, getHeight() * 0.2);
        renderer.setColor(new Color(98, 92, 85));
        renderer.text(title, -renderer.getTextWidth(title) / 2 + 3, -renderer.getTextHeight() / 2 + 3);
        renderer.setColor(new Color(38, 34, 25));
        renderer.text(title, -renderer.getTextWidth(title) / 2 + 0, -renderer.getTextHeight() / 2 + 0);
        renderer.popState();

        //
        Vector2 pos = new Vector2(xOffset + 250, getHeight() * 0.2 + 40);
        drawFieldTitle(renderer, "Joined Players:", pos);
        for (Client client : client.getClients()) {
            pos.y += 30;
            String text = client.getName();
            drawFieldText(renderer, text, pos);
            double rawX = pos.x;
            double width = renderer.getTextWidth(text) + 20;
            pos.x = Math.max(pos.x + 100, pos.x + width);

            if (client.getID() == 0) {
                drawFieldInfo(renderer, "[HOST]", pos);
            } else {
                drawFieldInfo(renderer, String.format("[%.1f", client.getLatency()) + "ms]", pos);
            }

            pos.x = rawX;
        }

    }

    @Override
    public void onTick() {
        super.onTick();

        client.tick();

        if (getWidth() > 1200) {
            xOffset += ((getWidth() - 1200) / 2d - xOffset) * Math.min(1, getDeltaTime() * 10);
        } else {
            xOffset -= xOffset * Math.min(1, getDeltaTime() * 10);
        }

        enterProgress += Math.min(0.1, getDeltaTime() * 3);
        enterProgress = Math.min(1, enterProgress);

        btnBack.onTick(getDeltaTime(), this);

        if (client.isClosed() || client.isCrashed()) {
            new Thread(this::showLog, "ShowLogThread").start();
            MenuStage menuStage = new MenuStage();
            menuStage.bgm = bgm;
            getWindow().changeStage(menuStage);
            client.shutdown();
        }

        if (client.getLocalController() != null) {
            client.getLocalController().tick();
        }
    }

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        if (client.getLocalController() != null) {
            client.getLocalController().onKeyPressed(keyCode);
        }
    }

    @Override
    public void onKeyReleased(int keyCode) {
        super.onKeyReleased(keyCode);
        if (client.getLocalController() != null) {
            client.getLocalController().onKeyReleased(keyCode);
        }
    }

    private void showLog() {
        String log = client.getCrashLog();
        JOptionPane.showConfirmDialog(
                getWindow().getJFrame(), "You have left the game.\n" + (log == null ? "" : log), "Left the game", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE
        );
    }

    @Override
    public void onMousePressed(double x, double y, int button) {
        super.onMousePressed(x, y, button);
        if (button == 1) {
            if (btnBack.getBounds().contains(x, y)) {
                SoundSystem.getInstance().getUISourceGroup().play("sounds/ui/mouse-click-0.wav");
                MenuStage stage = new MenuStage();
                stage.bgm = bgm;
                getWindow().changeStage(stage);
                this.client.shutdown();
            }
        }
    }

}
