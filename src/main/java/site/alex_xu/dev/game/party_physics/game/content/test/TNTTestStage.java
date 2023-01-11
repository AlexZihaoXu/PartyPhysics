package site.alex_xu.dev.game.party_physics.game.content.test;


import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Networking;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TNTTestStage extends Stage {

    public static void main(String[] args) {
        Networking.getInstance().init();
        PartyPhysicsWindow.getInstance().changeStage(new TNTTestStage());
        PartyPhysicsWindow.getInstance().getJFrame().setAlwaysOnTop(true);
        PartyPhysicsWindow.getInstance().start();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public void drawTNT(Renderer renderer, double x, double y) {
        renderer.pushState();
        renderer.setColor(new Color(236, 38, 74));
        renderer.rect(-0.095, -0.4, 0.19, 0.8, 0.1);
        renderer.translate(-0.24, 0);
        renderer.rect(-0.095, -0.4, 0.19, 0.8, 0.1);
        renderer.translate(0.48, 0);
        renderer.rect(-0.095, -0.4, 0.19, 0.8, 0.1);
        renderer.popState();

        renderer.pushState();

        renderer.setColor(68, 58, 56);

        renderer.translate(0, 0.02);
        renderer.rect(-0.38, -0.1 - 0.25, 0.76, 0.15, 0.1);
        renderer.rect(-0.38, -0.1 + 0.25, 0.76, 0.15, 0.1);
        renderer.rect(-0.35, -0.1, 0.7, 0.15, 0.07);

        renderer.setColor(201, 205, 229);
        renderer.rect(-0.25, -0.15, 0.5, 0.26, 0.05);
        renderer.setColor(Color.white);
        renderer.rect(-0.21, -0.11, 0.42, 0.18, 0.05);

        renderer.setColor(Color.black);
        renderer.scale(0.4);
        renderer.translate(-0.32, -0.32);
        renderer.rect(-0.15, 0.1, 0.3, 0.1, 0.03);
        renderer.rect(-0.05, 0.1, 0.1, 0.35, 0.03);

        renderer.translate(0.64, 0);
        renderer.rect(-0.15, 0.1, 0.3, 0.1, 0.03);
        renderer.rect(-0.05, 0.1, 0.1, 0.35, 0.03);
        renderer.translate(-0.32, 0);

        renderer.rect(-0.15, 0.1, 0.1, 0.35, 0.03);
        renderer.rect(0.05, 0.1, 0.1, 0.35, 0.03);

        renderer.translate(-0.15, 0.15);
        renderer.rotate(-0.56);
        renderer.rect(0, 0, 0.1, 0.35, 0.03);

        renderer.popState();
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);

        renderer.setColor(Color.white);
        renderer.clear();

        renderer.pushState();


        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.scale(80);
        renderer.rotate(getCurrentTime());

        drawTNT(renderer, 0, 0);

        renderer.popState();
    }

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        if (keyCode == KeyEvent.VK_SPACE) {

        }
    }

    @Override
    public void onTick() {
        super.onTick();


    }
}
