package site.alex_xu.dev.game.party_physics.game.graphics;

import java.awt.*;
import java.io.IOException;

public class PartyPhysicsWindow {
    final ActiveRenderingJFrame activeRenderingFrame;
    public PartyPhysicsWindow() {
        activeRenderingFrame = new ActiveRenderingJFrame("Party Physics!", this);
    }

    public void start() {
        try {
            onSetup();
            activeRenderingFrame.mainLoop();
            activeRenderingFrame.running = false;
        } finally {
            onDestroy();
        }
    }

    public void onSetup() {

    }

    public void onDestroy() {

    }

    public void onRender(Renderer renderer) {

        setMSAALevel(2);


        renderer.setColor(new Color(50, 50, 50));
        renderer.clear();
        renderer.setColor(Color.WHITE);

        renderer.pushState();

        renderer.translate(200, getHeight() / 2);

        renderer.pushState();
        renderer.rotate(getCurrentTime());
        renderer.rect(-50, -50, 100, 100);
        renderer.setColor(Color.RED);
        renderer.line(-50, -50, 0, 0);
        renderer.popState();

        renderer.translate(200, 0);

        renderer.pushState();
        renderer.rotate(getCurrentTime() / 2);
        renderer.rect(-50, -50, 100, 100);
        renderer.setColor(Color.RED);
        renderer.line(-50, -50, 0, 0);
        renderer.popState();

        renderer.translate(200, 0);

        renderer.pushState();
        renderer.rotate(getCurrentTime() / 5);
        renderer.rect(-50, -50, 100, 100);
        renderer.setColor(Color.RED);
        renderer.line(-50, -50, 0, 0);
        renderer.popState();

        renderer.translate(200, 0);

        renderer.pushState();
        renderer.rotate(getCurrentTime() / 10);
        renderer.rect(-50, -50, 100, 100);
        renderer.setColor(Color.RED);
        renderer.line(-50, -50, 0, 0);
        renderer.popState();

        renderer.popState();

        renderer.pushState();

        renderer.enableTextAA();
        renderer.setColor(0, 100, 0);

        renderer.scale(3 + Math.abs(getCurrentTime() % 5 - 2.5) / 2);
        renderer.popState();

        System.out.println(getDeltaTime());

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
