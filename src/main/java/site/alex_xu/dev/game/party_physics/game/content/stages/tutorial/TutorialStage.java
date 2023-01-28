package site.alex_xu.dev.game.party_physics.game.content.stages.tutorial;

import site.alex_xu.dev.game.party_physics.game.content.objects.items.GameObjectItemSMG;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.player.LocalPlayerController;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.graphics.Font;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class TutorialStage extends Stage {
    GameWorld world = new GameWorld();
    Camera camera = new Camera();
    double zoomProgress = 0.01;

    Player player;

    LocalPlayerController controller;

    @Override
    public void onLoad() {
        super.onLoad();

        world.init();
        world.addObject(new GameObjectGround(-60, 4, 220, 1));
        world.addObject(new GameObjectGround(-60, -2, 220, 1));
        world.addObject(new GameObjectGround(24, 2, 1.5, 2.5));
        world.addObject(new GameObjectBox(54, 0));
        world.addObject(new GameObjectItemSMG(74, 0));
        player = new Player(Color.white, 0, 0, 0);
        world.addPlayer(player);

        controller = new LocalPlayerController(player);
    }


    @Override
    public void onTick() {
        super.onTick();
        world.onTick();

        camera.scale += (Math.min(getWidth(), getHeight()) / 14d - camera.scale) * Math.min(1, getDeltaTime() * 3);
        camera.pos.x += (player.getPos().x - camera.pos.x) * Math.min(1, getDeltaTime() * 2);
        camera.pos.y += (player.getPos().y - camera.pos.y) * Math.min(1, getDeltaTime());

        controller.tick();
    }

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        controller.onKeyPressed(keyCode);
    }

    @Override
    public void onKeyReleased(int keyCode) {
        super.onKeyReleased(keyCode);
        controller.onKeyReleased(keyCode);
    }

    @Override
    public void onMousePressed(double x, double y, int button) {
        super.onMousePressed(x, y, button);
        controller.onMousePressed(x, y, button);
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);

        renderer.pushState();

        renderer.translate(getWidth() / 2, getHeight() / 2);
        double zoom;
        {
            zoomProgress += Math.min(0.05, getDeltaTime());
            zoomProgress = Math.min(1, zoomProgress);

            double x = 1 - zoomProgress;
            zoom = 1 - x * x * x * x * x;

        }
        renderer.scale(zoom);
        renderer.translate(-getWidth() / 2, -getHeight() / 2);

        renderer.setColor(211, 196, 172);
        renderer.clear();

        renderer.pushState();
        camera.applyTransform(renderer);
        renderer.setColor(38, 34, 25);
        renderer.setTextSize(1);
        renderer.setFont(Font.get("fonts/bulkypix.ttf"));
        renderer.text("TUTORIAL!!!", 0, 0);

        renderer.pushState();

        renderer.pushState();
        renderer.scale(0.4);
        renderer.text("Press <A> to move left. <D> to move right.", 1, 3);
        renderer.text("Continue moving to the right ...", 1, 7);
        renderer.popState();


        renderer.pushState();
        renderer.translate(20, 0);
        renderer.scale(0.4);
        renderer.text("Press <W> or <Space> to jump.", 1, 3);
        renderer.popState();

        renderer.pushState();
        renderer.translate(36, 0);
        renderer.scale(0.4);
        renderer.text("Press <S> or <Ctrl> to crouch.", 1, 3);
        renderer.popState();


        renderer.pushState();
        renderer.translate(48, 0);
        renderer.scale(0.4);
        renderer.text("Hold mouse left button to reach and grab.", 1, 3);
        renderer.popState();

        renderer.popState();

        world.onRender(renderer);

        renderer.popState();


        renderer.popState();

    }
}
