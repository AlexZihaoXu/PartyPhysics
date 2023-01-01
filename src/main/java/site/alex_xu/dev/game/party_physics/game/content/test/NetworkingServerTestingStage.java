package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.*;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Networking;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;
import java.awt.event.KeyEvent;

public class NetworkingServerTestingStage extends Stage {

    GameWorldServerManager world = new GameWorldServerManager();
    Camera camera = new Camera();

    Clock clock = new Clock();

    Player player;

    @Override
    public void onLoad() {
        super.onLoad();
        world.load();

        world.createGround(-30, 3, 60, 1);

        world.createGround(3, -2, 5, 0.5);
        player = world.createPlayer(0, -10, Color.WHITE);

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

        int moveX = 0;
        if (isKeyPressed(KeyEvent.VK_D)) {
            moveX++;
        }
        if (isKeyPressed(KeyEvent.VK_A)) {
            moveX--;
        }

        if (clock.elapsedTime() > 0.2) {
            clock.reset();
            world.createBox(Math.random() * 10 - 5, Math.random() * 3 - 20);
        }

        world.setPlayerMovementX(player, moveX);

    }

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_SPACE) {
            world.doPlayerJump(player);
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
