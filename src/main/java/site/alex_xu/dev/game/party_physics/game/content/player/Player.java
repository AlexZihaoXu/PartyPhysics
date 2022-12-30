package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.dynamics.joint.*;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

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

    MotorJoint<GameObject> armRightMotor1, armRightMotor2;
    MotorJoint<GameObject> armLeftMotor1, armLeftMotor2;

    WeldJoint<GameObject> footLeftJoint, footRightJoint;

    ArrayList<Joint<GameObject>> joints = new ArrayList<>();
    ArrayList<GameObjectPlayerPart> bodyParts = new ArrayList<>();


    double lastTouchGroundTime = 0;
    int moveDx = 0;

    GameObject groundObject = null;
    boolean touchGround = false;
    boolean sneak = false;

    Vector2 punchDirection = new Vector2();

    Color color;

    public Player(Color color, double x, double y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    private final Vector2 reachDirection = new Vector2(0, 0);

    Vector2 jumpPoint = null;

    public void setTouchGround(double now, GameObject gameObject, GameObject standObject, Vector2 jumpPoint) {
        if ((gameObject == legLeftEnd || gameObject == legRightEnd) && standObject != grabbingObject) {
            lastTouchGroundTime = now;
            touchGround = true;
            groundObject = standObject;
            this.jumpPoint = jumpPoint;
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
        armRightEnd = new GameObjectPlayerLimb(upperCenter.x + 0.35, upperCenter.y, upperCenter.x + 0.37 + 0.37, upperCenter.y);

        armLeftStart = new GameObjectPlayerLimb(upperCenter.x, upperCenter.y, upperCenter.x - 0.35, upperCenter.y);
        armLeftEnd = new GameObjectPlayerLimb(upperCenter.x - 0.35, upperCenter.y, upperCenter.x - 0.37 - 0.37, upperCenter.y);

        double legGap = 0.04;
        legLeftStart = new GameObjectPlayerLimb(lowerCenter.x - legGap, lowerCenter.y, lowerCenter.x - legGap, lowerCenter.y + 0.35);
        legLeftEnd = new GameObjectPlayerLimb(lowerCenter.x - legGap, lowerCenter.y + 0.35, lowerCenter.x - legGap, lowerCenter.y + 0.35 + 0.4);
        legRightStart = new GameObjectPlayerLimb(lowerCenter.x + legGap, lowerCenter.y, lowerCenter.x + legGap, lowerCenter.y + 0.35);
        legRightEnd = new GameObjectPlayerLimb(lowerCenter.x + legGap, lowerCenter.y + 0.35, lowerCenter.x + legGap, lowerCenter.y + 0.35 + 0.4);

        footLeft = new GameObjectPlayerFoot(lowerCenter.x - legGap, lowerCenter.y + 0.35 + 0.15);
        footRight = new GameObjectPlayerFoot(lowerCenter.x + legGap, lowerCenter.y + 0.35 + 0.15);

        armRightMotor1 = new MotorJoint<>(body, armRightStart);
        armRightMotor2 = new MotorJoint<>(armRightStart, armRightEnd);
        armLeftMotor1 = new MotorJoint<>(body, armLeftStart);
        armLeftMotor2 = new MotorJoint<>(armLeftStart, armLeftEnd);

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
        joints.add(armRightMotor1);
        joints.add(armRightMotor2);
        joints.add(armLeftMotor1);
        joints.add(armLeftMotor2);
        armRightMotor1.setMaximumForce(25);
        armRightMotor2.setMaximumForce(25);
        armRightMotor1.setCollisionAllowed(false);
        armRightMotor2.setCollisionAllowed(false);

        armLeftMotor1.setMaximumForce(25);
        armLeftMotor2.setMaximumForce(25);
        armLeftMotor1.setCollisionAllowed(false);
        armLeftMotor2.setCollisionAllowed(false);

        for (Joint<GameObject> joint : joints) {
            world.getSimulatedWorld().addJoint(joint);
        }

        for (GameObjectPlayerPart bodyPart : bodyParts) {
            bodyPart.setAngularDamping(20);
        }


        headBodyJoint.setLimitEnabled(false);

        for (GameObjectPlayerPart bodyPart : bodyParts) {
            bodyPart.color = this.color;
        }
    }

    public void punch(Vector2 direction) {
        if (punchDirection.distanceSquared(0, 0) < 0.001)
            punchDirection = direction.getNormalized();
    }

    public void jump() {
        if (isTouchingGround()) {
            touchGround = false;
            for (GameObjectPlayerPart bodyPart : bodyParts) {
                Vector2 vel = bodyPart.getLinearVelocity();
                bodyPart.setLinearVelocity(vel.x, vel.y - 12);
            }
            groundObject.applyImpulse(new Vector2(0, 10), jumpPoint);
        }
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
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

    public boolean isPunching() {
        return punchDirection.distanceSquared(0, 0) > 0.01;
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
            head.applyForce(Vector2.create(-10, body.getTransform().getRotationAngle() + Math.PI / 2));
            body.applyForce(Vector2.create(-30, body.getTransform().getRotationAngle() + Math.PI / 2));
            armLeftEnd.applyForce(Vector2.create(-5, body.getTransform().getRotationAngle()));
            armRightEnd.applyForce(Vector2.create(5, body.getTransform().getRotationAngle()));
            footLeft.applyForce(Vector2.create(20, body.getTransform().getRotationAngle() + Math.PI / 2 + 0.1));
            footRight.applyForce(Vector2.create(20, body.getTransform().getRotationAngle() + Math.PI / 2 - 0.1));
        }


        if (isTouchingGround()) {
            double maximumSpeed = 6;
            if (Math.abs(body.getLinearVelocity().x) < maximumSpeed) {
                head.applyForce(new Vector2(-moveDx * 10, -1));
                body.applyForce(new Vector2(50 * moveDx, 0));
            }
            double angle = body.getTransform().getRotationAngle();
            head.applyForce(Vector2.create(-angle * 30, angle));
            if (sneak) {
                body.applyForce(new Vector2(0, 120));
                footLeft.applyForce(new Vector2(0, -30));
                footRight.applyForce(new Vector2(0, -30));

            } else {
                if (moveDx == 0) {
                    Vector2 bodyPos = body.getWorldCenter();
                    Vector2 leftLegPos = legLeftEnd.getWorldCenter();
                    Vector2 rightLegPos = legRightEnd.getWorldCenter();
                    double centerX = (leftLegPos.x + rightLegPos.x) / 2;

                    double rate = Math.min(1, Math.abs(body.getTransform().getRotationAngle() / 0.08));
                    if (centerX > bodyPos.x) {
                        legLeftStart.applyTorque(4 * rate);
                        legRightStart.applyTorque(4 * rate);
                    } else {
                        legLeftStart.applyTorque(-4 * rate);
                        legRightStart.applyTorque(-4 * rate);
                    }
                }
            }

        } else {
            body.applyForce(new Vector2(10 * moveDx, 0));
        }


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
            armRightMotor1.setMaximumForce(20);
            armRightMotor2.setMaximumForce(20);
            armRightMotor1.setMaximumTorque(20);
            armRightMotor2.setMaximumTorque(20);

            double a = 0.5 * (reachDirection.x > 0 ? 1 : -1);
            armRightMotor1.setAngularTarget(angle + a / 2);
            armRightMotor2.setAngularTarget(-a / 2);
        } else {
            armRightMotor1.setMaximumForce(0);
            armRightMotor2.setMaximumForce(0);
            armRightMotor1.setMaximumTorque(0);
            armRightMotor2.setMaximumTorque(0);
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


        if (isPunching()) {

            armLeftEnd.applyImpulse(new Vector2(punchDirection.x * 0.8, punchDirection.y > 0 ? punchDirection.y : punchDirection.y * 0.1));
            legRightEnd.applyImpulse(new Vector2(-punchDirection.x * 0.15, punchDirection.y > 0 ? -punchDirection.y * 0.5 : -punchDirection.y * 0.05));
            legLeftEnd.applyImpulse(new Vector2(-punchDirection.x * 0.15, punchDirection.y > 0 ? -punchDirection.y * 0.5 : -punchDirection.y * 0.05));
            armLeftMotor1.setMaximumForce(20);
            armLeftMotor2.setMaximumForce(20);
            armLeftMotor1.setMaximumTorque(150);
            armLeftMotor2.setMaximumTorque(20);

            double rate = Math.abs(1 - (1- punchDirection.distance(0, 0)) * 1.3);
            double n = rate * 1.3;
            double angle = punchDirection.getDirection();
            if (Math.cos(angle) > 0) {
                armLeftMotor1.setAngularTarget(n + angle);
                armLeftMotor2.setAngularTarget(-n * 2);
            } else {
                armLeftMotor1.setAngularTarget(-n + angle);
                armLeftMotor2.setAngularTarget(n * 2);
            }
        } else {
            armLeftMotor1.setMaximumForce(0);
            armLeftMotor2.setMaximumForce(0);
            armLeftMotor1.setMaximumTorque(0);
            armLeftMotor2.setMaximumTorque(0);
        }
        punchDirection.x -= punchDirection.x * dt * 25;
        punchDirection.y -= punchDirection.y * dt * 25;

    }

    public Vector2 getPos() {
        return body.getWorldCenter();
    }

    public void tickPlayers(Player player) {
        if (isPunching()) {
            if (armLeftEnd.getWorldCenter().distance(player.getPos()) < 0.7) {
                player.body.applyImpulse(punchDirection.product(3));
            }
            if (armLeftEnd.getWorldCenter().distance(player.head.getWorldCenter()) < 0.3) {
                player.head.applyImpulse(punchDirection.product(6));
            }
        }
    }

    public void onRender(Renderer renderer) {
        for (GameObjectPlayerPart bodyPart : bodyParts) {
            bodyPart.onRender(renderer);
        }
    }



}
