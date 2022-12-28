package site.alex_xu.dev.game.party_physics.game.content.test;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.World;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;
import java.awt.event.KeyEvent;

public class PhysicsTestStage_Joints extends Stage {
    @Override
    public void onOffload() {
        super.onOffload();
        System.out.println(getClass().getSimpleName() + " offloaded!");
    }

    int updateCount = 0;

    Body infMass = new Body();
    World<Body> world = new World<>();

    // Bodies
    Body rope = new Body();
    Body rope2 = new Body();
    RevoluteJoint<Body> joint;
    RevoluteJoint<Body> joint2;

    //

    @Override
    public void onLoad() {
        super.onLoad();

        rope.addFixture(new Rectangle(20, 4));
        rope.setMass(MassType.NORMAL);

        rope2.addFixture(new Rectangle(20, 4));
        rope2.setMass(MassType.NORMAL);

        infMass.setMass(MassType.INFINITE);

        joint = new RevoluteJoint<>(infMass, rope, new Vector2(-10, 0));
        joint2 = new RevoluteJoint<>(rope, rope2, new Vector2(10, 0));

        world.addBody(rope);
        world.addBody(rope2);
        world.addBody(infMass);
        world.addJoint(joint);
        world.addJoint(joint2);

        rope2.translate(0, 10);

        world.setGravity(0, 9.8);
        System.out.println(getClass().getSimpleName() + " loaded!");
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        getWindow().setAALevel(2);

        renderer.pushState();

        renderer.translate(getWidth() / 2, getHeight() / 2);
        renderer.scale(4);

        renderer.setColor(new Color(200, 200, 200));
        renderer.clear();

        renderer.pushState();
        renderer.setColor(Color.RED);
        renderer.translate(rope.getTransform().getTranslation());
        renderer.rotate(rope.getTransform().getRotationAngle());
        renderer.rect(-10, -2, 20, 4);
        renderer.popState();

        renderer.pushState();
        renderer.setColor(Color.BLUE);
        renderer.translate(rope2.getTransform().getTranslation());
        renderer.rotate(rope2.getTransform().getRotationAngle());
        renderer.rect(-10, -2, 20, 4);
        renderer.popState();

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
    }

    @Override
    public void onKeyPressed(int keyCode) {
        super.onKeyPressed(keyCode);
        if (keyCode == KeyEvent.VK_SPACE) {
            if (joint != null) {
                world.removeJoint(joint);
                joint = null;
            }
        }
    }

    @Override
    public void onMouseMove(double x, double y) {

    }
}
