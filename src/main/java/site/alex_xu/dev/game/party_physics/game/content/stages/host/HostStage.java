package site.alex_xu.dev.game.party_physics.game.content.stages.host;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.generator.MapGenerator;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.content.stages.MultiplayerStage;
import site.alex_xu.dev.game.party_physics.game.content.stages.menu.MenuStage;
import site.alex_xu.dev.game.party_physics.game.content.ui.Button;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.HostingClient;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.HostingServer;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSource;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class HostStage extends MultiplayerStage {

    SoundSource bgm;
    Button btnBack = new Button("< menu");


    private double enterProgress = 0;
    private double xOffset = 0;

    private boolean ipAddressUpdateThreadShouldStop = false;
    private String[] ipAddressList = new String[0];

    private String crashLog = null;

    private final HostingServer server;

    private final Thread ipAddressUpdateThread = new Thread(() -> {
        while (!ipAddressUpdateThreadShouldStop) {
            try {
                InetAddress[] addresses = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
                ArrayList<String> addrs = new ArrayList<>();
                for (InetAddress address : addresses) {
                    if (address.getHostAddress().contains(":")) continue; // IPV6 (removed)
                    addrs.add(address.getHostAddress());
                }
                String[] newAddrs = new String[addrs.size()];
                for (int i = 0; i < addrs.size(); i++) {
                    newAddrs[i] = addrs.get(i);
                }
                ipAddressList = newAddrs;
                Thread.sleep(1000);
            } catch (UnknownHostException | InterruptedException e) {
                crashLog = e.getMessage();
                throw new RuntimeException(e);
            }
        }
    });

    public HostStage(String name, SoundSource bgmSource) {
        this.bgm = bgmSource;
        server = new HostingServer(name);
    }


    @Override
    public void onLoad() {
        super.onLoad();
        this.ipAddressUpdateThread.start();

        if (getWidth() > 1200) {
            xOffset = (getWidth() - 1200) / 2d;
        } else {
            xOffset = 0;
        }

        server.launch();

//        server.getWorldSyncer().syncAddGround(-500, 2.5, 1000, 1);
//        for (int i = 0; i < 10; i++) {
//            server.getWorldSyncer().syncAddObject(new GameObjectTNT(Math.random() * 100 - 50, -100));
//        }
//
        server.getSyncedWorld().onTick();
        server.getWorldSyncer().getGenerator().regenerate();
    }

    Camera camera = new Camera();

    Clock switchClock;

    @Override
    public void onOffload() {
        super.onOffload();
        ipAddressUpdateThreadShouldStop = true;

        server.shutdown();
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

        if (server.getSyncedWorld() != null && server.getSyncedWorld().hasPlayer(0)) {
            Player player = server.getSyncedWorld().getPlayer(0);
            camera.scale += (Math.min(getWidth(), getHeight()) / 14d - camera.scale) * Math.min(1, getDeltaTime() * 3);
            camera.pos.x += (player.getPos().x - camera.pos.x) * Math.min(1, getDeltaTime() * 2);
            camera.pos.y += (player.getPos().y - camera.pos.y) * Math.min(1, getDeltaTime());
        } else {
            camera.scale += (45 - camera.scale) * Math.min(1, getDeltaTime() * 5);
        }
        camera.render(server.getSyncedWorld(), renderer);

        btnBack.setPos(50 + xOffset, 50);
        btnBack.onRender(renderer);

        renderUIComponents(renderer);
    }

    public void renderUIComponents(Renderer renderer) {
        String title = "Host";

        renderer.pushState();
        renderer.translate(xOffset + 300, getHeight() * 0.2);
        renderer.setColor(new Color(98, 92, 85));
        renderer.text(title, -renderer.getTextWidth(title) / 2 + 3, -renderer.getTextHeight() / 2 + 3);
        renderer.setColor(new Color(38, 34, 25));
        renderer.text(title, -renderer.getTextWidth(title) / 2 + 0, -renderer.getTextHeight() / 2 + 0);
        renderer.popState();

        //

        Vector2 pos = new Vector2(xOffset + 280, getHeight() * 0.2 + 50);
        {
            drawFieldTitle(renderer, "IP Addresses:", pos);
            renderer.pushState();
            renderer.translate(20, 0);
            if (this.ipAddressList.length == 0) {
                pos.y += 30;
                drawFieldText(renderer, "EMPTY*", pos);
                pos.y += 30;
                drawFieldText(renderer, "PLEASE CHECK YOUR CONNECTION!", pos);
            } else {
                for (String addr : this.ipAddressList) {
                    pos.y += 30;
                    drawFieldText(renderer, addr, pos);
                }
                pos.y += 40;
            }
            renderer.popState();
        }
        {
            drawFieldTitle(renderer, "Joined Players:", pos);

            renderer.pushState();
            renderer.translate(20, 0);

            pos.y += 30;
            String text = server.getName();
            drawFieldText(renderer, text, pos);
            drawFieldInfo(renderer,
                    "[HOST]"
                    , Math.max(pos.x + 100, pos.x + renderer.getTextWidth(text)), pos.y + 1);
            pos.y += 30;
            if (this.server.getHostingClients().size() == 0) {
                pos.y += 10;
                drawFieldInfo(renderer, "(waiting for other players to join)", pos);
            } else {
                for (HostingClient client : this.server.getHostingClients()) {
                    text = client.getName() == null ? "connecting..." : client.getName();
                    drawFieldText(renderer, text, pos);
                    double rawX = pos.x;
                    pos.y += 1;
                    pos.x = Math.max(pos.x + 100, pos.x + renderer.getTextWidth(text));
                    drawFieldInfo(renderer,
                            "[" + client.getSocket().getSocket().getRemoteSocketAddress().toString() + "] [" + String.format("%.1f", client.getLatency()) + "ms]"
                            , pos);
                    pos.x = rawX;
                    pos.y += 29;
                }
            }

            renderer.popState();
        }

    }

    @Override
    public void onTick() {
        super.onTick();

        {
            double muffleShift = bgm.getMufflePercentage();
            muffleShift -= muffleShift * Math.min(1, getDeltaTime() * 3);
            bgm.setMufflePercentage(muffleShift);
        }
        {
            double muffleShiftTarget = getWindow().getJFrame().isActive() ? 0 : 1;
            double muffleShift = SoundSystem.getInstance().getMasterMuffle();
            muffleShift += (muffleShiftTarget - muffleShift) * Math.min(1, getDeltaTime() * 4);
            SoundSystem.getInstance().setMasterMuffle(muffleShift);
        }

//        if (clock.elapsedTime() > 0.05 && addCount < 20) {
//            addCount++;
//            clock.reset();
//            server.getWorldSyncer().syncAddBox(Math.random() * 10 - 5, -20 + Math.random() * 2);
//        } else if (clock.elapsedTime() > 0.3 && addCount < 23) {
//            addCount++;
//            clock.reset();
//            server.getWorldSyncer().syncAddObject(new GameObjectItemSMG(Math.random() * 3 - 1.5, -20 + Math.random() * 2));
//        }

        if (getWidth() > 1200) {
            xOffset += ((getWidth() - 1200) / 2d - xOffset) * Math.min(1, getDeltaTime() * 10);
        } else {
            xOffset -= xOffset * Math.min(1, getDeltaTime() * 10);
        }

        enterProgress += Math.min(0.1, getDeltaTime() * 3);
        enterProgress = Math.min(1, enterProgress);

        btnBack.onTick(getDeltaTime(), this);

        if (crashLog != null || server.isCrashed()) {
            MenuStage stage = new MenuStage();
            stage.bgm = bgm;
            getWindow().changeStage(stage);
            new Thread(this::showLog, "ShowLogThread").start();
        }

        server.tick();

        if (this.server.getSyncedWorld() != null) {
            GameWorld world = server.getSyncedWorld();
            if (world.getAlivePlayerCount() <= 1 && world.getPlayerCount() > 1) {
                if (switchClock == null) {
                    switchClock = new Clock();
                }
            }
        }

        if (switchClock != null) {
            if (switchClock.elapsedTime() > 2) {
                switchClock = null;
                server.getWorldSyncer().getGenerator().regenerate();
            }
        }

        try {
            Player player = server.getWorldSyncer().getLocalPlayerController().getPlayer();
            if (player != null) {
//                System.out.println(player.body.getRenderPos());
            }
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        if (server.getLocalPlayerController() != null) {
            server.getLocalPlayerController().onKeyPressed(keyCode);
        }

        if (keyCode == KeyEvent.VK_T) {
            server.getWorldSyncer().getGenerator().setSpawnRule(0, -3, 4);
            server.getWorldSyncer().getGenerator().repopulatePlayers();
        }
        if (keyCode == KeyEvent.VK_R) {
            server.getWorldSyncer().getGenerator().setSpawnRule(0, 0, 4);
            server.getWorldSyncer().getGenerator().repopulatePlayers();
        }
    }

    @Override
    public void onKeyReleased(int keyCode) {
        super.onKeyReleased(keyCode);
        if (server.getLocalPlayerController() != null) {
            server.getLocalPlayerController().onKeyReleased(keyCode);
        }
    }

    private void showLog() {
        String log;
        if (crashLog != null) {
            log = crashLog;
        } else {
            log = server.getServerCrashLog();
        }

        JOptionPane.showConfirmDialog(
                getWindow().getJFrame(), "Could not host game!\n" + log, "Server Crashed!", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE
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
            }
        }
        if (server.getLocalPlayerController() != null) {
            server.getLocalPlayerController().setCamera(camera);
            server.getLocalPlayerController().onMousePressed(x, y, button);
        }
    }
}
