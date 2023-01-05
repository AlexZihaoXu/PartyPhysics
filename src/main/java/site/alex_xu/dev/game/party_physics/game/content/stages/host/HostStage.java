package site.alex_xu.dev.game.party_physics.game.content.stages.host;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.stages.menu.MenuStage;
import site.alex_xu.dev.game.party_physics.game.content.ui.Button;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.HostingClient;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.HostingServer;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSource;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class HostStage extends Stage {

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
                    if (address.getHostAddress().contains(":")) continue; // IPV6
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
    }

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
                    , pos.x + renderer.getTextWidth(text) + 40, pos.y + 1);
            pos.y += 30;
            if (this.hostingServer.getClients().size() == 0) {
                pos.y += 10;
                drawFieldInfo(renderer, "(waiting for other players to join)", pos);
            } else {
                for (HostingClient client : this.hostingServer.getClients()) {
                    text = client.getName() == null ? "connecting..." : client.getName();
                    drawFieldText(renderer, text, pos);
                    drawFieldInfo(renderer,
                            "[" + client.getSocket().getSocket().getRemoteSocketAddress().toString() + "][" + String.format("%.1f", client.getLatency()) + "ms]"
                            , pos.x + renderer.getTextWidth(text) + 40, pos.y + 1);
                    pos.y += 30;
                }
            }

            renderer.popState();
        }

    }

    private void drawFieldTitle(Renderer renderer, String title, Vector2 pos) {
        drawFieldTitle(renderer, title, pos.x, pos.y);
    }

    private void drawFieldText(Renderer renderer, String text, Vector2 pos) {
        drawFieldText(renderer, text, pos.x, pos.y);
    }

    private void drawFieldInfo(Renderer renderer, String text, Vector2 pos) {
        drawFieldInfo(renderer, text, pos.x, pos.y);
    }

    private void drawFieldTitle(Renderer renderer, String title, double x, double y) {
        renderer.setFont("fonts/bulkypix.ttf");
        renderer.setTextSize(20);
        renderer.setColor(128, 120, 108);
        renderer.text(title, x + 2, y + 2);
        renderer.setColor(49, 44, 34);
        renderer.text(title, x, y);
    }

    private void drawFieldText(Renderer renderer, String text, double x, double y) {
        renderer.setFont("fonts/bulkypix.ttf");
        renderer.setTextSize(16);
        renderer.setColor(159, 149, 133);
        renderer.text(text, x + 2, y + 2);
        renderer.setColor(73, 66, 51);
        renderer.text(text, x, y);
    }


    private void drawFieldInfo(Renderer renderer, String text, double x, double y) {
        renderer.setFont("fonts/bulkypix.ttf");
        renderer.setTextSize(14);
        renderer.setColor(new Color(168, 158, 141));
        renderer.text(text, x + 2, y + 2);
        renderer.setColor(new Color(101, 93, 72));
        renderer.text(text, x, y);
    }

    @Override
    public void onTick() {
        super.onTick();

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
