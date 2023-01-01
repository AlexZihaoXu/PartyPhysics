package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.game.engine.framework.*;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Networking;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;

public class NetworkingServerTestingStage extends Stage {

    GameWorldServerManager world = new GameWorldServerManager();
    Camera camera = new Camera();

    Clock clock = new Clock();

    @Override
    public void onLoad() {
        super.onLoad();
        world.load();

        world.createGround(-10, 3, 20, 1);

    }

    @Override
    public void onOffload() {
        super.onOffload();
        world.offload();
    }

    @Override
    public void onTick() {
        super.onTick();
        world.onTick();

        if (clock.elapsedTime() > 0.2) {
            clock.reset();
            world.createBox(Math.random() * 10 - 5, -15);
        }
    }

    @Override
    public void onRender(Renderer renderer) {

        camera.scale += (50 - camera.scale) * Math.min(1, getDeltaTime() * 5);
        camera.pos.y += (-2 - camera.pos.y) * Math.min(1, getDeltaTime() * 5);

        super.onRender(renderer);
        renderer.setColor(new Color(11, 43, 93));
        renderer.clear();
        camera.render(world.getWorld(), renderer);
    }

    public static void main(String[] args) {
        Networking.getInstance().init();
        PartyPhysicsWindow.getInstance().changeStage(new NetworkingServerTestingStage());
        PartyPhysicsWindow.getInstance().start();
    }
}
