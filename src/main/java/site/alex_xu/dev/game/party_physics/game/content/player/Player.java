package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.dynamics.Force;
import org.dyn4j.dynamics.joint.*;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;

import java.awt.*;
import java.util.ArrayList;

public class Player {

    private final double x;
    private final double y;
    public GameObjectPlayerHead head;
    public GameObjectPlayerBody body;
    GameObjectPlayerLimb armRightStart, armRightEnd, armLeftStart, armLeftEnd;
    GameObjectPlayerLimb legRightStart, legRightEnd, legLeftStart, legLeftEnd;

    GameObjectPlayerFoot footLeft, footRight;

    RevoluteJoint<GameObject> headBodyJoint;
    RevoluteJoint<GameObject> rightArmBodyJoint, rightArmJoint, leftArmBodyJoint, leftArmJoint;
    RevoluteJoint<GameObject> rightLegBodyJoint, rightLegJoint, leftLegBodyJoint, leftLegJoint;

    MotorJoint<GameObject> armMotor1, armMotor2;

    WeldJoint<GameObject> footLeftJoint, footRightJoint;

    ArrayList<Joint<GameObject>> joints = new ArrayList<>();
    ArrayList<GameObjectPlayerPart> bodyParts = new ArrayList<>();


    double lastTouchGroundTime = 0;
    int moveDx = 0;
    boolean touchGround = false;

    Color color;

    public Player(Color color, double x, double y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    private final Vector2 reachDirection = new Vector2(0, 0);

    public void setTouchGround(double now, GameObject gameObject, GameObject standObject) {
        if ((gameObject == legLeftEnd || gameObject == legRightEnd) && standObject != grabbingObject) {
            lastTouchGroundTime = now;
            touchGround = true;
        }
    }

    private GameObject grabbingObject = null;
    private WeldJoint<GameObject> grabbingJoint = null;

    public void tryGrabItem(GameObject grabbed, GameObject bodyPart) {
        if (grabbingJoint == null && reachDirection.distanceSquared(0, 0) > 0.1) {
            if (bodyPart == this.armRightEnd) {
                grabbingObject = grabbed;
                grabbingJoint = new WeldJoint<>(armRightEnd, grabbed, armRightEnd.getWorldCenter());
                head.getWorld().getSimulatedWorld().addJoint(grabbingJoint);
            }
        }
    }

    public boolean isTouchingGround() {
        return touchGround && (head.getCurrentTime() - lastTouchGroundTime < 20.0 / PhysicsSettings.TICKS_PER_SECOND);
    }

    public void initPhysics(GameWorld world) {
        double x = this.x;
        double y = this.y;
        head = new GameObjectPlayerHead(x, y);
        body = new GameObjectPlayerBody(x, y + GameObjectPlayerHead.r + GameObjectPlayerBody.h / 2 + 0.02);

        Vector2 upperCenter = new Vector2(x, y + GameObjectPlayerHead.r + 0.05 + GameObjectPlayerLimb.r);
        Vector2 lowerCenter = new Vector2(x, y + GameObjectPlayerHead.r + 0.05 + GameObjectPlayerBody.h - GameObjectPlayerLimb.r);

        armRightStart = new GameObjectPlayerLimb(upperCenter.x, upperCenter.y, upperCenter.x + 0.35, upperCenter.y);
        armRightEnd = new GameObjectPlayerLimb(upperCenter.x + 0.35, upperCenter.y, upperCenter.x + 0.35 + 0.35, upperCenter.y);

        armLeftStart = new GameObjectPlayerLimb(upperCenter.x, upperCenter.y, upperCenter.x - 0.35, upperCenter.y);
        armLeftEnd = new GameObjectPlayerLimb(upperCenter.x - 0.35, upperCenter.y, upperCenter.x - 0.35 - 0.35, upperCenter.y);

        double legGap = 0.04;
        legLeftStart = new GameObjectPlayerLimb(lowerCenter.x - legGap, lowerCenter.y, lowerCenter.x - legGap, lowerCenter.y + 0.35);
        legLeftEnd = new GameObjectPlayerLimb(lowerCenter.x - legGap, lowerCenter.y + 0.35, lowerCenter.x - legGap, lowerCenter.y + 0.35 + 0.4);
        legRightStart = new GameObjectPlayerLimb(lowerCenter.x + legGap, lowerCenter.y, lowerCenter.x + legGap, lowerCenter.y + 0.35);
        legRightEnd = new GameObjectPlayerLimb(lowerCenter.x + legGap, lowerCenter.y + 0.35, lowerCenter.x + legGap, lowerCenter.y + 0.35 + 0.4);

        footLeft = new GameObjectPlayerFoot(lowerCenter.x - legGap, lowerCenter.y + 0.35 + 0.15);
        footRight = new GameObjectPlayerFoot(lowerCenter.x + legGap, lowerCenter.y + 0.35 + 0.15);

        armMotor1 = new MotorJoint<>(body, armRightStart);
        armMotor2 = new MotorJoint<>(armRightStart, armRightEnd);

        bodyParts.add(head);
        bodyParts.add(body);
        bodyParts.add(armRightStart);
        bodyParts.add(armRightEnd);
        bodyParts.add(armLeftStart);
        bodyParts.add(armLeftEnd);
        bodyParts.add(legLeftStart);
        bodyParts.add(legLeftEnd);
        bodyParts.add(legRightStart);
        bodyParts.add(legRightEnd);
        bodyParts.add(footLeft);
        bodyParts.add(footRight);
        for (GameObjectPlayerPart bodyPart : bodyParts) {
            world.addObject(bodyPart);
            bodyPart.player = this;
        }

        headBodyJoint = new RevoluteJoint<>(head, body, new Vector2(x, y + (GameObjectPlayerHead.r + 0.05) / 2));
        leftArmBodyJoint = new RevoluteJoint<>(body, armLeftStart, upperCenter);
        leftArmJoint = new RevoluteJoint<>(armLeftStart, armLeftEnd, new Vector2(upperCenter.x - 0.35, upperCenter.y));
        rightArmBodyJoint = new RevoluteJoint<>(body, armRightStart, upperCenter);
        rightArmJoint = new RevoluteJoint<>(armRightStart, armRightEnd, new Vector2(upperCenter.x + 0.35, upperCenter.y));
        leftLegBodyJoint = new RevoluteJoint<>(body, legLeftStart, new Vector2(lowerCenter.x - legGap, lowerCenter.y));
        rightLegBodyJoint = new RevoluteJoint<>(body, legRightStart, new Vector2(lowerCenter.x + legGap, lowerCenter.y));
        leftLegJoint = new RevoluteJoint<>(legLeftStart, legLeftEnd, new Vector2(lowerCenter.x - legGap, lowerCenter.y + 0.35));
        rightLegJoint = new RevoluteJoint<>(legRightStart, legRightEnd, new Vector2(lowerCenter.x + legGap, lowerCenter.y + 0.35));
        footLeftJoint = new WeldJoint<>(legLeftEnd, footLeft, new Vector2(lowerCenter.x - legGap, lowerCenter.y + 0.35 + 0.4));
        footRightJoint = new WeldJoint<>(legRightEnd, footRight, new Vector2(lowerCenter.x + legGap, lowerCenter.y + 0.35 + 0.4));

        joints.add(headBodyJoint);
        joints.add(leftArmJoint);
        joints.add(leftArmBodyJoint);
        joints.add(rightArmJoint);
        joints.add(rightArmBodyJoint);
        joints.add(leftLegBodyJoint);
        joints.add(leftLegJoint);
        joints.add(rightLegBodyJoint);
        joints.add(rightLegJoint);
        joints.add(footLeftJoint);
        joints.add(footRightJoint);
        joints.add(armMotor1);
        joints.add(armMotor2);
        armMotor1.setMaximumForce(25);
        armMotor2.setMaximumForce(25);
        armMotor1.setCollisionAllowed(false);
        armMotor2.setCollisionAllowed(false);

        for (Joint<GameObject> joint : joints) {
            world.getSimulatedWorld().addJoint(joint);
        }

        for (GameObjectPlayerPart bodyPart : bodyParts) {
            bodyPart.setAngularDamping(20);
        }


        headBodyJoint.setLimitEnabled(true);
        headBodyJoint.setLimits(-2, 2);

        for (GameObjectPlayerPart bodyPart : bodyParts) {
            bodyPart.color = this.color;
        }
    }

    public void jump() {
        if (isTouchingGround()) {
            touchGround = false;
            for (GameObjectPlayerPart bodyPart : bodyParts) {
                Vector2 vel = bodyPart.getLinearVelocity();
                bodyPart.setLinearVelocity(vel.x, vel.y - 13);
            }
        }
    }

    public void setMoveDirection(int dx) {
        moveDx = dx;
    }

    public int getMoveDirection() {
        return moveDx;
    }

    public void setReachDirection(Vector2 reachDirection) {
        this.reachDirection.set(reachDirection);
    }

    public void onPhysicsTick(double dt, double now) {


        if (touchGround && (now - lastTouchGroundTime < 0.2)) {
            footLeft.applyForce(new Vector2(0, 60));
            footRight.applyForce(new Vector2(0, 60));
            legLeftStart.applyForce(new Vector2(0, -50));
            legRightStart.applyForce(new Vector2(0, -50));
            head.applyForce(new Vector2(0, -50));
            if (footLeft.getWorldCenter().distanceSquared(footRight.getWorldCenter()) < 0.015) {
                Vector2 v = footLeft.getWorldCenter().subtract(footRight.getWorldCenter()).getNormalized();
                footLeft.applyForce(new Vector2(v.x * 0.1, v.y * 0.1));
                footRight.applyForce(new Vector2(-v.x * 0.1, -v.y * 0.1));
                body.applyImpulse(new Vector2(0, 1));
                footLeft.applyImpulse(new Vector2(-0.2, -0.5));
                footRight.applyImpulse(new Vector2(0.2, -0.5));
            }
        } else {
            head.applyForce(Vector2.create(-20, body.getTransform().getRotationAngle() + Math.PI / 2));
            armLeftEnd.applyForce(Vector2.create(-5, body.getTransform().getRotationAngle()));
            armRightEnd.applyForce(Vector2.create(5, body.getTransform().getRotationAngle()));
            footLeft.applyForce(Vector2.create(10, body.getTransform().getRotationAngle() + Math.PI / 2 - 0.2));
            footRight.applyForce(Vector2.create(10, body.getTransform().getRotationAngle() + Math.PI / 2 + 0.2));
        }

        Vector2 vel = body.getLinearVelocity();
        if (isTouchingGround())
            head.applyForce(new Vector2(-moveDx * 10, -1));
        body.setLinearVelocity(vel.x + (moveDx * 10 - vel.x) * dt * 35, vel.y);

        if (moveDx != 0) {
            rightLegJoint.setLimitEnabled(true);
            rightLegBodyJoint.setLimitEnabled(true);
            rightLegJoint.setReferenceAngle(0);
            rightLegBodyJoint.setReferenceAngle(0);

            leftLegJoint.setLimitEnabled(true);
            leftLegBodyJoint.setLimitEnabled(true);
            leftLegJoint.setReferenceAngle(0);
            leftLegBodyJoint.setReferenceAngle(0);
            double duration = 0.2;
            double ratec = Math.cos(now / duration * Math.PI) * 0.5 + 0.5;
            double rates = Math.sin(now / duration * Math.PI) * 0.5 + 0.5;
            double angle, angle2;
            angle = 0.2 + ratec * 2.2;
            angle2 = rates * 1.8;

            if (moveDx < 0) {
                angle2 *= -1;
                angle = Math.PI - angle;
            }

            angle *= -1;
            angle2 *= -1;

            double gap = 0.1;

            rightLegBodyJoint.setLimits(angle - gap, angle + gap);
            rightLegJoint.setLimits(angle2 - gap, angle2 + gap);

            angle = 0.2 + (1 - ratec) * 2.2;
            angle2 = (1 - rates) * 1.8;

            if (moveDx < 0) {
                angle2 *= -1;
                angle = Math.PI - angle;
            }

            angle *= -1;
            angle2 *= -1;

            leftLegBodyJoint.setLimits(angle - gap, angle + gap);
            leftLegJoint.setLimits(angle2 - gap, angle2 + gap);

        } else {
            rightLegJoint.setLimitEnabled(false);
            rightLegBodyJoint.setLimitEnabled(false);
            leftLegJoint.setLimitEnabled(false);
            leftLegBodyJoint.setLimitEnabled(false);
        }
        double armDamping = 5;

        if (reachDirection.distanceSquared(0, 0) > 0.1) {
            body.applyForce(new Vector2(reachDirection.x * 0.3, reachDirection.y));
            double angle = -reachDirection.getAngleBetween(new Vector2(0, 0));
            armMotor1.setMaximumForce(5);
            armMotor2.setMaximumForce(5);
            armMotor1.setMaximumTorque(20);
            armMotor2.setMaximumTorque(20);

            double a = 0.5 * (reachDirection.x > 0 ? 1 : -1);
            armMotor1.setAngularTarget(angle + a / 2);
            armMotor2.setAngularTarget(-a / 2);
        } else {
            armMotor1.setMaximumForce(0);
            armMotor2.setMaximumForce(0);
            armMotor1.setMaximumTorque(0);
            armMotor2.setMaximumTorque(0);
            if (grabbingJoint != null) {
                head.getWorld().getSimulatedWorld().removeJoint(grabbingJoint);
                grabbingJoint = null;
                grabbingObject = null;
            }
        }


        armLeftStart.setAngularDamping(armDamping);
        armRightStart.setAngularDamping(armDamping);
        armLeftEnd.setAngularDamping(armDamping * 1.5);
        armRightEnd.setAngularDamping(armDamping * 1.5);
    }

    public Vector2 getPos() {
        return body.getWorldCenter();
    }

}
