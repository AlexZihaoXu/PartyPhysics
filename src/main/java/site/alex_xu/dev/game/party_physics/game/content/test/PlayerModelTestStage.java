package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectWoodPlank;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PlayerModelTestStage extends Stage {
    GameWorld world = new GameWorld();
    Player player;
    double cameraX = 0;

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

        player = new Player(-3, -10);
        world.addPlayer(player);

    }

    @Override
    public void onTick() {
        super.onTick();
        world.onTick();
        cameraX += (player.getPos().x - cameraX) * Math.min(1, getDeltaTime() * 3);

        int direction = 0;
        if (isKeyPressed(KeyEvent.VK_LEFT)) {
            direction--;
        }
        if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            direction++;
        }
        player.setMoveDirection(direction);

    }

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        if (keyCode == KeyEvent.VK_UP) {
            player.jump();
        }
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        renderer.setColor(new Color(206, 222, 219));
        renderer.clear();

        renderer.pushState();
        renderer.translate(getWidth() / 2d, getHeight() / 2d + 20);
        renderer.scale(60);
        renderer.translate(-cameraX, 0);
        world.onRender(renderer);
        renderer.popState();

        renderer.setColor(Color.GREEN);
        renderer.text("X: " + cameraX, 5, 5);
    }
}
