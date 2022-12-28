package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.listener.ContactListener;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.awt.*;
import java.util.ArrayList;

public class Player {

    private double x, y;
    public GameObjectPlayerHead head;
    public GameObjectPlayerBody body;
    GameObjectPlayerLimb armRightStart, armRightEnd, armLeftStart, armLeftEnd;
    GameObjectPlayerLimb legRightStart, legRightEnd, legLeftStart, legLeftEnd;

    DistanceJoint<GameObject> headBodyJoint;
    RevoluteJoint<GameObject> rightArmBodyJoint, rightArmJoint, leftArmBodyJoint, leftArmJoint;
    RevoluteJoint<GameObject> rightLegBodyJoint, rightLegJoint, leftLegBodyJoint, leftLegJoint;

    ArrayList<Joint<GameObject>> joints = new ArrayList<>();
    ArrayList<GameObjectPlayerPart> bodyParts = new ArrayList<>();

    double lastTouchGroundTime = 0;
    boolean touchGround = false;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setTouchGround(double now) {
        lastTouchGroundTime = now;
        touchGround = true;
    }

    public boolean isTouchingGround() {
        return touchGround && (head.getCurrentTime() - lastTouchGroundTime < 0.01);
    }

    public void initPhysics(GameWorld world) {
        double x = this.x;
        double y = this.y;
        head = new GameObjectPlayerHead(x, y);
        body = new GameObjectPlayerBody(x, y + GameObjectPlayerHead.r + GameObjectPlayerBody.h / 2 + 0.01);

        Vector2 upperCenter = new Vector2(x, y + GameObjectPlayerHead.r + 0.05 + GameObjectPlayerLimb.r + 0.05);
        Vector2 lowerCenter = new Vector2(x, y + GameObjectPlayerHead.r + 0.05 + GameObjectPlayerBody.h - GameObjectPlayerLimb.r);

        armRightStart = new GameObjectPlayerLimb(upperCenter.x, upperCenter.y, upperCenter.x + 0.35, upperCenter.y);
        armRightEnd = new GameObjectPlayerLimb(upperCenter.x + 0.35, upperCenter.y, upperCenter.x + 0.35 + 0.35, upperCenter.y);

        armLeftStart = new GameObjectPlayerLimb(upperCenter.x, upperCenter.y, upperCenter.x - 0.35, upperCenter.y);
        armLeftEnd = new GameObjectPlayerLimb(upperCenter.x - 0.35, upperCenter.y, upperCenter.x - 0.35 - 0.35, upperCenter.y);

        double legGap = 0.055;
        legLeftStart = new GameObjectPlayerLimb(lowerCenter.x - legGap, lowerCenter.y, lowerCenter.x - legGap, lowerCenter.y + 0.35);
        legLeftEnd = new GameObjectPlayerLimb(lowerCenter.x - legGap, lowerCenter.y + 0.35, lowerCenter.x - legGap, lowerCenter.y + 0.35 + 0.4);
        legRightStart = new GameObjectPlayerLimb(lowerCenter.x + legGap, lowerCenter.y, lowerCenter.x + legGap, lowerCenter.y + 0.35);
        legRightEnd = new GameObjectPlayerLimb(lowerCenter.x + legGap, lowerCenter.y + 0.35, lowerCenter.x + legGap, lowerCenter.y + 0.35 + 0.4);

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
        for (GameObjectPlayerPart bodyPart : bodyParts) {
            world.addObject(bodyPart);
            bodyPart.player = this;
        }

        headBodyJoint = new DistanceJoint<>(head, body, new Vector2(x, y), new Vector2(x, y + GameObjectPlayerHead.r + 0.05));
        leftArmBodyJoint = new RevoluteJoint<>(body, armLeftStart, upperCenter);
        leftArmJoint = new RevoluteJoint<>(armLeftStart, armLeftEnd, new Vector2(upperCenter.x - 0.35, upperCenter.y));
        rightArmBodyJoint = new RevoluteJoint<>(body, armRightStart, upperCenter);
        rightArmJoint = new RevoluteJoint<>(armRightStart, armRightEnd, new Vector2(upperCenter.x + 0.35, upperCenter.y));
        leftLegBodyJoint = new RevoluteJoint<>(body, legLeftStart, new Vector2(lowerCenter.x - legGap, lowerCenter.y));
        rightLegBodyJoint = new RevoluteJoint<>(body, legRightStart, new Vector2(lowerCenter.x + legGap, lowerCenter.y));
        leftLegJoint = new RevoluteJoint<>(legLeftStart, legLeftEnd, new Vector2(lowerCenter.x - legGap, lowerCenter.y + 0.35));
        rightLegJoint = new RevoluteJoint<>(legRightStart, legRightEnd, new Vector2(lowerCenter.x + legGap, lowerCenter.y + 0.35));

        joints.add(headBodyJoint);
        joints.add(leftArmJoint);
        joints.add(leftArmBodyJoint);
        joints.add(rightArmJoint);
        joints.add(rightArmBodyJoint);
        joints.add(leftLegBodyJoint);
        joints.add(leftLegJoint);
        joints.add(rightLegBodyJoint);
        joints.add(rightLegJoint);

        for (Joint<GameObject> joint : joints) {
            world.getSimulatedWorld().addJoint(joint);
        }

        head.setAngularDamping(200);
        body.setAngularDamping(100);
        armLeftEnd.setAngularDamping(80);
        armRightEnd.setAngularDamping(80);
        legLeftStart.setAngularDamping(80);
        legRightStart.setAngularDamping(80);
        legLeftEnd.setAngularDamping(20);
        legRightEnd.setAngularDamping(20);
    }

    public void jump() {
        if (isTouchingGround()) {
            touchGround = false;
            head.applyImpulse(new Vector2(0, -4));
            body.applyImpulse(new Vector2(0, -2));
            armLeftStart.applyImpulse(new Vector2(-0.15, 0));
            armRightStart.applyImpulse(new Vector2(0.15, 0));
        }
    }


    public void onPhysicsTick(double dt, double now) {
        if (touchGround && (now - lastTouchGroundTime < 0.2)) {
            head.applyForce(new Vector2(0, -15));
            body.applyForce(new Vector2(0, -13));

            Vector2 pos;
            double angle;

            pos = legLeftEnd.getWorldCenter();
            angle = legLeftEnd.getTransform().getRotationAngle();
            pos.add(new Vector2(legLeftEnd.length, 0).rotate(angle));
            legLeftEnd.applyForce(new Vector2(-0.15, 10), pos);

            pos = legRightEnd.getWorldCenter();
            angle = legRightEnd.getTransform().getRotationAngle();
            pos.add(new Vector2(legRightEnd.length, 0).rotate(angle));
            legRightEnd.applyForce(new Vector2(0.15, 10));

            legLeftStart.applyForce(new Vector2(-0.1, 0));
            legRightStart.applyForce(new Vector2(0.1, 0));

            armLeftEnd.applyForce(new Vector2(0, 2));
            armRightEnd.applyForce(new Vector2(0, 2));

            {
                Vector2 legLeftPos = legLeftEnd.getWorldPoint(new Vector2(0, 0.3));
                Vector2 legRightPos = legRightEnd.getWorldPoint(new Vector2(0, 0.3));
                if (legLeftPos.distanceSquared(legRightPos) < 0.02) {
                    legRightStart.setAngularVelocity(legRightEnd.getAngularVelocity() - 2);
                    legLeftStart.setAngularVelocity(legRightEnd.getAngularVelocity() + 2);

                    legLeftEnd.applyImpulse(new Vector2(-0.01, -0.02), legLeftEnd.getWorldPoint(new Vector2(0, -0.3)));
                    legRightEnd.applyImpulse(new Vector2(0.01, -0.02), legRightEnd.getWorldPoint(new Vector2(0, -0.3)));
                }
            }

        } else {
            head.applyForce(new Vector2(0, -0.5));
            legLeftEnd.applyForce(new Vector2(0, 0.4));
            legRightEnd.applyForce(new Vector2(0, 0.4));
        }

    }
}
