package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Camera {

    public static class Shake {
        private double createdTime;
        private final double x;
        private final double y;

        private final double speed;

        private final boolean gunShake;


        public Shake(double magnitude, double direction, double speed, boolean gunShake) {
            x = Math.cos(direction) * magnitude;
            y = Math.sin(direction) * magnitude;
            this.speed = speed;
            this.gunShake = gunShake;
            createdTime = Clock.currentTime();
        }

        public Vector2 getOffsets(double now) {
            double magnitude = getMagnitudeWhen(now);
            return new Vector2(x * magnitude, y * magnitude);
        }

        public double getMagnitudeWhen(double now) {
            double z = (now - createdTime) * speed;
            if (gunShake)
                z = Math.sqrt(z);
            if (Math.abs(z - Math.PI) <= 1e-9) {
                z = 1e-9;
            }
            return Math.sin(z) / (z - Math.PI);
        }

        public boolean isFinished(double now) {
            double z = (now - createdTime) * speed;
            if (gunShake)
                z = Math.sqrt(z);
            double leftXInt = Math.floor(z / Math.PI);
            double rightXInt = Math.ceil(z / Math.PI);
            double mid = (leftXInt + rightXInt) / 2;
            return 1 / mid < 0.05;
        }
    }

    private final HashSet<Shake> shakes = new HashSet<>();

    public Vector2 pos = new Vector2();
    public double scale = 1.0;

    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    public double getWidth() {
        return getWindow().getWidth();
    }

    public double getHeight() {
        return getWindow().getHeight();
    }

    public void render(GameWorld world, Renderer renderer) {
        renderer.pushState();
        applyTransform(renderer);
        world.onRender(renderer);
        renderer.popState();
        renderPlayerNameTag(world, renderer);
    }

    public void renderPlayerNameTag(GameWorld world, Renderer renderer) {
        renderer.pushState();

        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.setFont("fonts/bulkypix.ttf");

        for (Player player : world.players.values()) {
            String displayName = player.getDisplayName();
            if (displayName != null) {
                renderer.pushState();

                Color color = player.getColor().darker();
                renderer.setColor(color.getRed(), color.getGreen(), color.getBlue(), 225);
                Vector2 pos = player.head.getRenderPos().copy();
                pos.y -= 0.35;
                renderer.translate(
                        (pos.x - this.pos.x) * scale, (pos.y - this.pos.y) * scale
                );

                renderer.triangle(-10, -9, 10, -9, 0, 0);
                renderer.translate(0, -9);

                renderer.text(
                        displayName,
                        -renderer.getTextWidth(displayName) / 2, -renderer.getTextHeight()
                );

                renderer.popState();
            }
        }

        renderer.popState();
    }

    public void addShake(double magnitude, double direction, double speed, boolean gunShake) {
        shakes.add(new Shake(magnitude, direction, speed, gunShake));
    }

    public void applyTransform(Renderer renderer) {
        Vector2 shakeOffset = new Vector2();
        ArrayList<Shake> removed = new ArrayList<>();
        double now = Clock.currentTime();
        for (Shake shake : shakes) {
            if (shake.isFinished(now)) {
                removed.add(shake);
            }
            Vector2 offset = shake.getOffsets(now);
            shakeOffset.x += offset.x;
            shakeOffset.y += offset.y;
        }
        for (Shake shake : removed) {
            shakes.remove(shake);
        }
        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.translate(shakeOffset);
        renderer.scale(scale);
        renderer.translate(-pos.x, -pos.y);
    }

    public Vector2 getWorldMousePos() {
        Vector2 mouse = getWindow().getMousePos();
        return new Vector2(
                (mouse.x - getWidth() / 2) / scale + pos.x,
                (mouse.y - getHeight() / 2) / scale + pos.y
        );
    }

}
