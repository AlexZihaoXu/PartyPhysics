package site.alex_xu.dev.game.party_physics.game.engine.framework;

import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class Stage {
    public Stage() {

    }

    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    public int getWidth() {
        return getWindow().getWidth();
    }

    public int getHeight() {
        return getWindow().getHeight();
    }

    public double getDeltaTime() {
        return getWindow().getDeltaTime();
    }

    public double getCurrentTime() {
        return getWindow().getCurrentTime();
    }


    public void onLoad() {

    }

    public void onOffload() {

    }

    public void onTick() {

    }

    public void onRender(Renderer renderer) {
    }

}
