package site.alex_xu.dev.game.party_physics.game.render;

import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

class ActiveRenderingJFrame extends JFrame implements WindowListener {

    final Canvas canvas;
    final BufferStrategy bufferStrategy;

    public boolean running;

    int width;
    int height;

    double dt;

    double now;

    int msaaScale = 2;

    PartyPhysicsWindow partyPhysicsWindow;

    ActiveRenderingJFrame(String title, PartyPhysicsWindow window) {
        super(title);
        partyPhysicsWindow = window;
        setIgnoreRepaint(true);

        canvas = new Canvas();
        canvas.setIgnoreRepaint(true);
        canvas.setSize(600, 400);
        add(canvas);
        pack();

        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();

        width = canvas.getWidth();
        height = canvas.getHeight();

        addWindowListener(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    void mainLoop() {
        if (running)
            throw new IllegalStateException("The loop is already running!");

        running = true;
        setVisible(true);

        Clock clock = new Clock();

        double lastTickTime = clock.elapsedTime();


        VolatileImage msaaBuffer = null;

        while (running) {
            now = clock.elapsedTime();
            dt = now - lastTickTime;
            lastTickTime = now;

            width = canvas.getWidth();
            height = canvas.getHeight();

            partyPhysicsWindow.onTick();

            if (msaaBuffer != null) {
                int result = msaaBuffer.validate(getGraphicsConfiguration());
                if (result == VolatileImage.IMAGE_INCOMPATIBLE) {
                    msaaBuffer = null;
                }
            }

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
            if (msaaScale == 1) {
                Renderer renderer = new Renderer(g, width, height);
                partyPhysicsWindow.onRender(renderer);
            } else {
                if (msaaBuffer == null || msaaBuffer.getWidth() != width * msaaScale || msaaBuffer.getHeight() != height * msaaScale) {
                    msaaBuffer = getGraphicsConfiguration().createCompatibleVolatileImage(width * msaaScale, height * msaaScale);
                    msaaBuffer.setAccelerationPriority(1);
                }
                Graphics2D g2 = msaaBuffer.createGraphics();
                Renderer renderer = new Renderer(g2, msaaBuffer.getWidth(), msaaBuffer.getHeight());
                renderer.scale(msaaScale);
                partyPhysicsWindow.onRender(renderer);
                g2.dispose();

                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(msaaBuffer, 0, 0, width, height, this);

            }

            if (!this.bufferStrategy.contentsLost()) {
                this.bufferStrategy.show();
            }
            Thread.yield();
        }
    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        running = false;
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
