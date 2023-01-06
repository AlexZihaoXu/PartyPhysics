package site.alex_xu.dev.game.party_physics.game.content.stages.host;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.content.stages.MultiplayerStage;
import site.alex_xu.dev.game.party_physics.game.content.stages.menu.MenuStage;
import site.alex_xu.dev.game.party_physics.game.content.ui.Button;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.HostingClient;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.HostingServer;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSource;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import javax.swing.*;
import java.awt.*;
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

    private final HostingServer hostingServer;

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
        hostingServer = new HostingServer(name);
    }

    Clock clock = new Clock();

    @Override
    public void onLoad() {
        super.onLoad();
        this.ipAddressUpdateThread.start();

        if (getWidth() > 1200) {
            xOffset = (getWidth() - 1200) / 2d;
        } else {
            xOffset = 0;
        }

        hostingServer.launch();

        hostingServer.getWorldSyncer().syncAddGround(-50, 2.5, 100, 1);
    }

    Camera camera = new Camera();

    @Override
    public void onOffload() {
        super.onOffload();
        ipAddressUpdateThreadShouldStop = true;

        hostingServer.shutdown();
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

        if (hostingServer.getSyncedWorld() != null && hostingServer.getSyncedWorld().hasPlayer(0)) {
            Player player = hostingServer.getSyncedWorld().getPlayer(0);
            camera.scale += (Math.min(getWidth(), getHeight()) / 14d - camera.scale) * Math.min(1, getDeltaTime() * 3);
            camera.pos.x += (player.getPos().x - camera.pos.x) * Math.min(1, getDeltaTime() * 2);
            camera.pos.y += (player.getPos().y - camera.pos.y) * Math.min(1, getDeltaTime());
        } else {
            camera.scale += (45 - camera.scale) * Math.min(1, getDeltaTime() * 5);
        }
        camera.render(hostingServer.getSyncedWorld(), renderer);

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
            String text = hostingServer.getName();
            drawFieldText(renderer, text, pos);
            drawFieldInfo(renderer,
                    "[HOST]"
                    , Math.max(pos.x + 100, pos.x + renderer.getTextWidth(text)), pos.y + 1);
            pos.y += 30;
            if (this.hostingServer.getHostingClients().size() == 0) {
                pos.y += 10;
                drawFieldInfo(renderer, "(waiting for other players to join)", pos);
            } else {
                for (HostingClient client : this.hostingServer.getHostingClients()) {
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

        if (clock.elapsedTime() > 1.5) {
            clock.reset();
            hostingServer.getWorldSyncer().syncAddBox(Math.random() - 0.5, -20 + Math.random() * 2);
        }

        if (getWidth() > 1200) {
            xOffset += ((getWidth() - 1200) / 2d - xOffset) * Math.min(1, getDeltaTime() * 10);
        } else {
            xOffset -= xOffset * Math.min(1, getDeltaTime() * 10);
        }

        enterProgress += Math.min(0.1, getDeltaTime() * 3);
        enterProgress = Math.min(1, enterProgress);

        btnBack.onTick(getDeltaTime(), this);

        if (crashLog != null || hostingServer.isCrashed()) {
            MenuStage stage = new MenuStage();
            stage.bgm = bgm;
            getWindow().changeStage(stage);
            new Thread(this::showLog, "ShowLogThread").start();
        }

        hostingServer.tick();
    }

    private void showLog() {
        String log;
        if (crashLog != null) {
            log = crashLog;
        } else {
            log = hostingServer.getServerCrashLog();
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
    }
}
