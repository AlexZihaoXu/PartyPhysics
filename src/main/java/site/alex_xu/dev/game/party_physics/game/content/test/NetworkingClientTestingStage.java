package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorldClientManager;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.networking.*;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class NetworkingClientTestingStage extends Stage {

    Camera camera = new Camera();
    GameWorldClientManager world = new GameWorldClientManager();

    @Override
    public void onLoad() {
        super.onLoad();
        world.load();
    }

    @Override
    public void onTick() {
        super.onTick();
        world.onTick();
    }

    @Override
    public void onOffload() {
        super.onOffload();
        world.offload();
    }

    @Override
    public void onRender(Renderer renderer) {

        camera.scale += (50 - camera.scale) * Math.min(1, getDeltaTime() * 5);
        camera.pos.y += (-2 - camera.pos.y) * Math.min(1, getDeltaTime() * 5);

        super.onRender(renderer);
        renderer.setColor(new Color(11, 43, 93));
        renderer.clear();
        camera.render(world.getWorld(), renderer);

        renderer.pushState();
        renderer.setColor(new Color(0, 100, 0));
        renderer.text("RX: " + world.getRXPKGSpeed() + " pkg/sec ", 5, 5);
        renderer.popState();
    }

    public static void main(String[] args) {
        Networking.getInstance().init();
        PartyPhysicsWindow.getInstance().changeStage(new NetworkingClientTestingStage());
        PartyPhysicsWindow.getInstance().start();
    }
}
