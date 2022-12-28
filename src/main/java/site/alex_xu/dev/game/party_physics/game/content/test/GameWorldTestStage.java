package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectWoodPlank;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;


public class GameWorldTestStage extends Stage {
    GameWorld world = new GameWorld();

    @Override
    public void onLoad() {
        super.onLoad();
        getWindow().setAALevel(2);
        world.init();
        world.addObject(new GameObjectGround(-20, 2, 40, 1));
        world.addObject(new GameObjectBox(0.5, -20));
        world.addObject(new GameObjectBox(0, -40));
        world.addObject(new GameObjectWoodPlank(-2, -50, 4, 0.5));
    }

    Clock boxAddTimer = new Clock();
    @Override
    public void onTick() {
        super.onTick();
        world.onTick();

        if (boxAddTimer.elapsedTime() > 0.2) {
            boxAddTimer.reset();
            world.addObject(new GameObjectBox(
                    Math.random() * 2 - 1, -20 + Math.random()
            ));
        }
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        renderer.setColor(new Color(230, 238, 241));
        renderer.clear();

        renderer.pushState();

        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.scale(45);
        world.onRender(renderer);

        renderer.popState();


        renderer.pushState();
        renderer.setColor(new Color(0, 150, 0));
        renderer.text("Count: " + world.getObjectsCount(), 5, 5);
        renderer.text("Delta: " + String.format("%.3f ms", getDeltaTime() * 1000), 5, 25);
        renderer.popState();
    }
}
