package site.alex_xu.dev.game.party_physics.game.render;

import java.awt.*;

public class PartyPhysicsWindow {
    private final ActiveRenderingJFrame activeRenderingFrame;

    public PartyPhysicsWindow() {
        activeRenderingFrame = new ActiveRenderingJFrame("Party Physics!", this);

    }

    public void start() {
        activeRenderingFrame.mainLoop();
    }


    public void onRender(Renderer renderer) {
        renderer.setColor(0);
        renderer.clear();

        renderer.pushState();

        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.rotate(getCurrentTime() * 0.1);
        renderer.setColor(Color.WHITE);
        renderer.rect(-100, -100, 200, 200);

        renderer.popState();

    }

    public void onTick() {

    }

    // Setters

    public void setMSAALevel(int level) {
        activeRenderingFrame.msaaLevel = level;
    }

    // Getters

    public int getWidth() {
        return activeRenderingFrame.width;
    }

    public int getHeight() {
        return activeRenderingFrame.height;
    }

    public double getDeltaTime() {
        return activeRenderingFrame.dt;
    }

    public double getCurrentTime() {
        return activeRenderingFrame.now;
    }

    public int getMSAALevel() {
        return activeRenderingFrame.msaaLevel;
    }

}
