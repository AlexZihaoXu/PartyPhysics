package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.game.content.level.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.level.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;


public class GameWorldTestStage extends Stage {
    GameWorld world = new GameWorld();

    @Override
    public void onLoad() {
        super.onLoad();
        getWindow().setMSAALevel(2);
        world.init();
        world.addObject(new GameObjectGround(-10, 2, 20, 1));
        world.addObject(new GameObjectBox(-1.5, -20));
        world.addObject(new GameObjectBox(0, -40));
    }

    @Override
    public void onTick() {
        super.onTick();
        world.onTick();
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        renderer.setColor(new Color(230, 238, 241));
        renderer.clear();

        renderer.pushState();

        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.scale(30);
        world.onRender(renderer);

        renderer.popState();
    }
}
