package site.alex_xu.dev.game.party_physics.game.content.test;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.items.GameObjectItemSMG;
import site.alex_xu.dev.game.party_physics.game.content.objects.items.GameObjectItemPistol;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.event.KeyEvent;

public class ItemTestingStage extends Stage {
    GameWorld world = new GameWorld();
    Player player;
    Camera camera = new Camera();

    @Override
    public void onLoad() {
        super.onLoad();
        getWindow().setAALevel(2);
        world.init();
        world.addObject(new GameObjectGround(-100, 6, 200, 0.5));

        player = new Player(new Color(0, 0, 0, 255), -3, -10);
        world.addPlayer(player);
        world.addPlayer(new Player(new Color(176, 167, 0, 255), -3, -10));
        world.addObject(new GameObjectBox(-6, -1));
        world.addObject(new GameObjectBox(-6, 0));
//        for (int i = -1; i < 2; i++) {
//            world.addPlayer(new Player(new Color(0, 25, 0, 255), -3 + i * 3, -10));
//        }

//        for (int i = -3; i < 3; i++) {
//            world.addObject(new GameObjectBox(i * 1.5, i + Math.sin(i * 0.3) * 3));
//        }

        for (int i = -2; i < 2; i++) {
            world.addObject(new GameObjectItemPistol(i  * 5, -10 - i));
            world.addObject(new GameObjectItemSMG(i  * 5 + 3, -10 - i));
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
            if (player.getHoldItem() != null) {
                player.getHoldItem().use();
            }
            Vector2 pos = player.body.getWorldPoint(new Vector2(0, -0.35));
            pos = camera.getWorldMousePos().subtract(pos).getNormalized();
            player.setReachDirection(Vector2.create(1, pos.getDirection()));
        } else if (player.getHoldItem() == null) {
            player.setReachDirection(new Vector2(0, 0));
        } else {
            Vector2 pos = player.body.getWorldPoint(new Vector2(0, -0.35));
            pos = camera.getWorldMousePos().subtract(pos).getNormalized();
            player.setReachDirection(pos);
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
        if (keyCode == KeyEvent.VK_F) {
            player.setReachDirection(new Vector2(0, 0));
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
        renderer.setColor(106, 122, 119);
        renderer.clear();
        camera.render(world, renderer);

        renderer.pushState();
        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.scale(50);

        renderer.setColor(Color.RED);

        renderer.popState();
    }
}
