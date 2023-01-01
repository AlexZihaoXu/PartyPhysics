package site.alex_xu.dev.game.party_physics.game.content.test;

import org.dyn4j.geometry.Vector2;
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
        world.setPlayerSneak(player, isKeyPressed(KeyEvent.VK_S));

        if (clock.elapsedTime() > 1) {
            clock.reset();
            world.createBox(Math.random() * 10 - 5, Math.random() * 3 - 20);
        }

        if (getMouseButton(1)) {
//            Vector2 mouse = camera.getWorldMousePos();
//            Vector2 chest = player.body.getWorldPoint(new Vector2(0, -0.2));
            Vector2 mouse = getMousePos();
            Vector2 center = new Vector2(getWidth() / 2f, getHeight() / 2f);
            double angle = mouse.subtract(center).getDirection() - player.body.getTransform().getRotationAngle();
            world.setReachDirection(player, Vector2.create(1, angle));
        } else {
            world.setReachDirection(player, new Vector2());
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
        super.onRender(renderer);

        camera.scale += (70 - camera.scale) * Math.min(1, getDeltaTime() * 5);
        camera.pos.y += (player.getPos().y - camera.pos.y) * Math.min(1, getDeltaTime());
        camera.pos.x += (player.getPos().x - camera.pos.x) * Math.min(1, getDeltaTime() * 5);

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
