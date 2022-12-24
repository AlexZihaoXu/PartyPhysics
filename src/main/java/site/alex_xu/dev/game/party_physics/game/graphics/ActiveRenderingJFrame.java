package site.alex_xu.dev.game.party_physics.game.graphics;

import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.VolatileImage;

class ActiveRenderingJFrame extends JFrame implements WindowListener {

    final Canvas canvas;
    final BufferStrategy bufferStrategy;

    public boolean running;

    int width;
    int height;

    double dt;

    double now;

    int msaaLevel = 2;

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

        VolatileImage[] msaaBuffers = null;

        int renderScale = 1;
        while (running) {
            now = clock.elapsedTime();
            dt = now - lastTickTime;
            lastTickTime = now;

            width = canvas.getWidth();
            height = canvas.getHeight();

            partyPhysicsWindow.onTick();

            Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
            if (msaaLevel == 0) {
                renderScale = 1;
                Renderer renderer = new Renderer(g, width, height);
                partyPhysicsWindow.onRender(renderer);
            } else {
                if (msaaBuffers == null || msaaBuffers.length != msaaLevel || msaaBuffers[0].validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE || msaaBuffers[0].getWidth() / 2 != width || msaaBuffers[0].getHeight() / 2 != height) {
                    renderScale = 2;
                    msaaBuffers = new VolatileImage[msaaLevel];
                    msaaBuffers[0] = getGraphicsConfiguration().createCompatibleVolatileImage(width * 2, height * 2);
                    for (int i = 1; i < msaaLevel; i++) {
                        msaaBuffers[i] = getGraphicsConfiguration().createCompatibleVolatileImage(msaaBuffers[i - 1].getWidth() * 2, msaaBuffers[i - 1].getHeight() * 2);
                        msaaBuffers[i].setAccelerationPriority(1);
                        renderScale *= 2;
                    }
                }

                VolatileImage lastBuffer = msaaBuffers[msaaBuffers.length - 1];
                Graphics2D g2 = lastBuffer.createGraphics();
                Renderer renderer = new Renderer(g2, lastBuffer.getWidth(), lastBuffer.getHeight());
                renderer.scale(renderScale);
                partyPhysicsWindow.onRender(renderer);

                for (int i = msaaBuffers.length - 1; i > 0; i--) {
                    VolatileImage bufferSmall = msaaBuffers[i - 1];
                    VolatileImage bufferLarge = msaaBuffers[i];

                    Graphics2D g3 = bufferSmall.createGraphics();
                    g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g3.drawImage(bufferLarge, 0, 0, bufferSmall.getWidth(), bufferSmall.getHeight(), this);
                    g3.dispose();
                }
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(msaaBuffers[0], 0, 0, width, height, this);

                g2.dispose();

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