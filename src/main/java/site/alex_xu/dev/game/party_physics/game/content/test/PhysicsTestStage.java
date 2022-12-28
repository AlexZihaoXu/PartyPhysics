package site.alex_xu.dev.game.party_physics.game.content.test;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.*;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.world.World;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;
import java.util.ArrayList;

public class PhysicsTestStage extends Stage {

    // Physics

    World<Body> world = new World<>();
    Body ground = new Body();

    ArrayList<Body> balls = new ArrayList<>();

    int updateCount = 0;

    // -Physics

    @Override
    public void onLoad() {
        super.onLoad();
        System.out.println(getClass().getSimpleName() + " loaded!");
        getWindow().setAALevel(2);

        world.setGravity(0, 98);

        for (int i = 0; i < 10; i++) {

            double x = Math.random() * 100 - 50;
            double y = Math.random() * -50;

            Body ball = new Body();
            Circle circle = new Circle(4);
            BodyFixture fixture = new BodyFixture(circle);
            fixture.setRestitution(0.4);
            fixture.setFriction(0.8);
            ball.addFixture(fixture);
            ball.setMass(MassType.NORMAL);
            world.addBody(ball);
            ball.translate(x, y);
            balls.add(ball);
        }


        Rectangle rectangle = new Rectangle(500, 5);
        ground.addFixture(rectangle);
        ground.setMass(MassType.INFINITE);
        ground.translate(0, 100);

        world.addBody(ground);

    }

    Clock addClock = new Clock();

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);

        if (addClock.elapsedTime() > 0.15) {
            double x = Math.random() * 25 - 12.5;
            double y = Math.random() * -50 - 1000;

            Body ball = new Body();
            Circle circle = new Circle(4);
            BodyFixture fixture = new BodyFixture(circle);
            fixture.setRestitution(0.4);
            fixture.setFriction(0.8);
            ball.addFixture(fixture);
            ball.setMass(MassType.NORMAL);
            world.addBody(ball);
            ball.translate(x, y);
            balls.add(ball);
            addClock.reset();
        }

        renderer.setColor(Color.BLACK);
        renderer.clear();

        renderer.pushState();

        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.scale(2);

        for (int i = 0; i < balls.size(); i++) {
            Body ball = balls.get(i);
            renderer.pushState();
            renderer.translate(ball.getWorldCenter());
            renderer.rotate(ball.getTransform().getRotationAngle());
            renderer.setColor(Color.WHITE);
            renderer.circle(0, 0, 4);
            renderer.setColor(Color.BLACK);
            renderer.setLineWidth(0.3);
            renderer.line(0, 0, 4, 0);
            renderer.popState();
        }

        renderer.setColor(new Color(150, 255, 150));
        renderer.translate(ground.getWorldCenter());
        renderer.rect(-250, -2.5, 500, 5);

        renderer.popState();

        renderer.pushState();

        renderer.setColor(Color.GREEN);
        renderer.text("Count: " + balls.size(), 5, 5);
        renderer.text("Delta: " + String.format("%.3f ms", getDeltaTime() * 1000), 5, 25);

        renderer.popState();
    }

    @Override
    public void onTick() {
        super.onTick();

        int targetUpdateCount = (int) (getCurrentTime() / (1d / PhysicsSettings.TICKS_PER_SECOND));
        while (updateCount < targetUpdateCount) {
            world.updatev(1d / PhysicsSettings.TICKS_PER_SECOND);
            updateCount++;
        }

        ArrayList<Body> removed = new ArrayList<>();
        for (Body ball : balls) {
            if (ball.getTransform().getTranslationY() > getHeight() + 1000) {
                removed.add(ball);
            }
        }
        for (Body body : removed) {
            balls.remove(body);
        }

    }

    @Override
    public void onOffload() {
        super.onOffload();
        System.out.println(getClass().getSimpleName() + " offloaded!");
    }
}
