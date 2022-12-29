package site.alex_xu.dev.game.party_physics.game.content.test;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectWoodPlank;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PlayerModelTestStage extends Stage {
    GameWorld world = new GameWorld();
    Player player;
    Camera camera = new Camera();

    @Override
    public void onLoad() {
        super.onLoad();
        getWindow().setAALevel(2);
        world.init();
        // 6
        world.addObject(new GameObjectGround(-100, 6, 200, 0.5));

        world.addObject(new GameObjectBox(4, 5 - GameObjectBox.size));
        world.addObject(new GameObjectBox(-4, 5 - GameObjectBox.size));
        world.addObject(new GameObjectBox(4, 6 - GameObjectBox.size));
        world.addObject(new GameObjectBox(-4, 6 - GameObjectBox.size));

        world.addObject(new GameObjectWoodPlank(-6, 5 - GameObjectBox.size - 0.6, 12, 0.6));
        for (int y = 1; y <= 4; y++) {
            for (int i = 1; i <= 4 - y; i++) {
                world.addObject(new GameObjectBox(y * 0.5 + i, 5 - GameObjectBox.size - 0.6 - y));
            }
        }
        for (int i = 1; i <= 4; i++) {
            world.addObject(new GameObjectBox(-5, 5 - GameObjectBox.size - 0.6 - i));
        }

        player = new Player(new Color(99, 194, 42, 255), -3, -10);
        world.addPlayer(player);
        for (int i = -1; i < 1; i++) {
            world.addPlayer(new Player(new Color((int) (127 + Math.random() * 128), (int) (127 + Math.random() * 128), (int) (127 + Math.random() * 128), 255), i * 0.3 + Math.random() *2.5, -10));
        }

    }

    @Override
    public void onTick() {
        super.onTick();
        world.onTick();
        camera.scale += (60 - camera.scale) * Math.min(1, getDeltaTime() * 5);
        camera.pos.x += (player.getPos().x - camera.pos.x) * Math.min(1, getDeltaTime() * 3);
        camera.pos.y += (player.getPos().y - camera.pos.y) * Math.min(1, getDeltaTime() * 0.5);

        int direction = 0;
        if (isKeyPressed(KeyEvent.VK_A)) {
            direction--;
        }
        if (isKeyPressed(KeyEvent.VK_D)) {
            direction++;
        }
        player.setMoveDirection(direction);
        if (getMouseButton(1)) {
            Vector2 pos = player.body.getWorldCenter();
            pos = camera.getWorldMousePos().subtract(pos).getNormalized();
            player.setReachDirection(pos);
        } else {
            player.setReachDirection(new Vector2(0, 0));
        }
    }

    @Override
    public void onMousePressed(double x, double y, int button) {
        super.onMousePressed(x, y, button);
        if (button == 3) {
            Vector2 pos = player.body.getWorldCenter();
            pos = camera.getWorldMousePos().subtract(pos).getNormalized();
            player.punch(pos);
        }
    }

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        if (keyCode == KeyEvent.VK_W) {
            player.jump();
        }
        if (keyCode == KeyEvent.VK_S) {
            player.setSneak(true);
        }
    }

    @Override
    public void onKeyReleased(int keyCode) {
        super.onKeyReleased(keyCode);
        if (keyCode == KeyEvent.VK_S) {
            player.setSneak(false);
        }
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        renderer.setColor(new Color(206, 222, 219));
        renderer.clear();

        camera.render(world, renderer);

        renderer.setColor(Color.GREEN.darker());
        renderer.text("Dt = " + String.format("%.2f ms", getWindow().getVideoDt() * 1000), 5, 5);
        renderer.text("AA.level = " + getWindow().getAALevel(), 5, 35);
    }
}
