package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.dynamics.joint.*;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectItem;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
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

    private double health = 1.0;

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = Math.min(1, Math.max(0, health));
    }

    GameObject groundObject = null;
    boolean touchGround = false;
    boolean sneak = false;

    Vector2 punchDirection = new Vector2();

    Color color;

    private final int id;
    private double moveDuration = 0;

    private boolean loaded = false;
    private double latencyDurationOffset = 0;

    public Player(Color color, double x, double y, int id) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public int getID() {
        return id;
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

    private String displayName = null;

    private boolean grabItemSynced = false;

    public boolean isDead() {
        return health <= 0;
    }

    public boolean isAlive() {
        return !isDead();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void tryGrabItem(GameObject grabbed, GameObject bodyPart) {
        try {
            if (grabbingJoint == null && reachDirection.distanceSquared(0, 0) > 0.1 && isAlive()) {
                if (bodyPart == this.armRightEnd) {
                    if (grabbed instanceof GameObjectGround) return;
                    grabbingObject = grabbed;
                    if (grabbed instanceof GameObjectItem) {
                        Vector2 point = armRightEnd.getWorldPoint(new Vector2(0.2, 0));
                        grabbed.getTransform().setTranslation(point);
                        grabbed.getTransform().setRotation(armRightEnd.getTransform().getRotationAngle());
                        grabbingJoint = new WeldJoint<>(armRightEnd, grabbed, point);
                        ((GameObjectItem) grabbed).setHoldPlayer(this);
                        ((GameObjectItem) grabbed).forceUpdateModel(reachDirection.x < 0);
                    }
                    grabbingJoint = new WeldJoint<>(armRightEnd, grabbed, armRightEnd.getWorldCenter());
                    head.getWorld().getSimulatedWorld().addJoint(grabbingJoint);
                }
            }
        } catch (IllegalArgumentException ignored) {
            cancelGrabbing();
        }
    }

    public boolean isTouchingGround() {
        return touchGround && (head.getCurrentTime() - lastTouchGroundTime < 20.0 / PhysicsSettings.TICKS_PER_SECOND);
    }

    public void offloadPhysics(GameWorld world) {
        for (Joint<GameObject> joint : joints) {
            world.getSimulatedWorld().removeJoint(joint);
        }
        for (GameObjectPlayerPart bodyPart : bodyParts) {
            world.removeObject(bodyPart);
        }
        if (grabbingJoint != null) {
            world.getSimulatedWorld().removeJoint(grabbingJoint);
            if (grabbingObject instanceof GameObjectItem) {
                ((GameObjectItem) grabbingObject).setHoldPlayer(null);
                ((GameObjectItem) grabbingObject).forceUpdateModel(((GameObjectItem) grabbingObject).isFlipped());
            }
        }
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

        double legGap = 0.032;
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
        body.setAngularDamping(30);


        headBodyJoint.setLimitsEnabled(false);

        for (GameObjectPlayerPart bodyPart : bodyParts) {
            bodyPart.color = this.color;
        }

        loaded = true;
    }

    public void punch(Vector2 direction) {
        if (punchDirection.distanceSquared(0, 0) < 0.001)
            punchDirection = direction.getNormalized();
    }

    public void jump() {
        if (isTouchingGround() && !isDead()) {
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

    public void setMovementX(int dx) {
        moveDx = dx;
    }

    public int getMovementX() {
        return moveDx;
    }

    public void setReachDirection(Vector2 reachDirection) {
        this.reachDirection.set(reachDirection);
    }

    public boolean isPunching() {
        return punchDirection.distanceSquared(0, 0) > 0.01;
    }

    public void onPhysicsTick(double dt, double now) {

        if (this.getPos().y > 50) {
            this.setHealth(0);
        }

        if (isDead()) {
            cancelGrabbing();
            return;
        }

        if (touchGround && (now - lastTouchGroundTime < 0.2)) {
            head.applyForce(new Vector2(0, -100));
            footLeft.applyForce(new Vector2(0, 50));
            footRight.applyForce(new Vector2(0, 50));
            head.applyForce(Vector2.create(-10, body.getTransform().getRotationAngle() + Math.PI / 2));
            body.applyForce(Vector2.create(-60, body.getTransform().getRotationAngle() + Math.PI / 2));
            footLeft.applyForce(Vector2.create(30, body.getTransform().getRotationAngle() + Math.PI / 2 + 0.1));
            footRight.applyForce(Vector2.create(30, body.getTransform().getRotationAngle() + Math.PI / 2 - 0.1));
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
            head.applyForce(Vector2.create(-angle * 15, angle));
            if (sneak) {
                body.applyForce(new Vector2(0, 80));
                legLeftStart.applyForce(new Vector2(0, 50));
                legRightStart.applyForce(new Vector2(0, 50));
                footLeft.applyForce(new Vector2(0, -70));
                footRight.applyForce(new Vector2(0, -70));

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


        latencyDurationOffset += (GameObject.latency - latencyDurationOffset) * Math.min(1, dt * 3);
        if (moveDx != 0) {
            legLeftStart.cancelSync = true;
            legLeftEnd.cancelSync = true;
            legRightStart.cancelSync = true;
            legRightEnd.cancelSync = true;
            this.moveDuration += dt;
            double moveDuration = this.moveDuration + latencyDurationOffset;
            rightLegJoint.setLimitsEnabled(true);
            rightLegBodyJoint.setLimitsEnabled(true);
            rightLegJoint.setLimitsReferenceAngle(0);
            rightLegBodyJoint.setLimitsReferenceAngle(0);

            leftLegJoint.setLimitsEnabled(true);
            leftLegBodyJoint.setLimitsEnabled(true);
            leftLegJoint.setLimitsReferenceAngle(0);
            leftLegBodyJoint.setLimitsReferenceAngle(0);
            double duration = 0.2;
            double ratec = Math.cos(moveDuration / duration * Math.PI) * 0.5 + 0.5;
            double rates = Math.sin(moveDuration / duration * Math.PI) * 0.5 + 0.5;
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
            legLeftStart.cancelSync = false;
            legLeftEnd.cancelSync = false;
            legRightStart.cancelSync = false;
            legRightEnd.cancelSync = false;
            moveDuration = 0;
            rightLegJoint.setLimitsEnabled(false);
            rightLegBodyJoint.setLimitsEnabled(false);
            leftLegJoint.setLimitsEnabled(false);
            leftLegBodyJoint.setLimitsEnabled(false);
        }
        double armDamping = 5;

        if (reachDirection.distanceSquared(0, 0) > 0.1) {
            body.applyForce(new Vector2(reachDirection.x * 0.3, reachDirection.y));
            double angle = -reachDirection.getAngleBetween(new Vector2(0, 0));
            armRightMotor1.setMaximumForce(12);
            armRightMotor2.setMaximumForce(15);
            armRightMotor1.setMaximumTorque(12);
            armRightMotor2.setMaximumTorque(15);

            double a = 0.5 * (reachDirection.x > 0 ? 1 : -1);
            armRightMotor1.setAngularTarget(angle + a / 2);
            armRightMotor2.setAngularTarget(-a / 2);

            if (getHoldItem() != null) {
                getHoldItem().setFlipped(reachDirection.x < 0);
            }
        } else {
            armRightMotor1.setMaximumForce(0);
            armRightMotor2.setMaximumForce(0);
            armRightMotor1.setMaximumTorque(0);
            armRightMotor2.setMaximumTorque(0);
            if (grabbingJoint != null) {
                cancelGrabbing();
            }
        }

        armLeftStart.setAngularDamping(armDamping);
        armRightStart.setAngularDamping(armDamping);
        armLeftEnd.setAngularDamping(armDamping * 1.5);
        armRightEnd.setAngularDamping(armDamping * 1.5);


        if (isPunching()) {

            armLeftEnd.applyImpulse(new Vector2(punchDirection.x * 1.5, punchDirection.y > 0 ? punchDirection.y : punchDirection.y * 0.1));
            legRightEnd.applyImpulse(new Vector2(-punchDirection.x * 0.5, punchDirection.y > 0 ? -punchDirection.y * 0.5 : -punchDirection.y * 0.05));
            legLeftEnd.applyImpulse(new Vector2(-punchDirection.x * 0.5, punchDirection.y > 0 ? -punchDirection.y * 0.5 : -punchDirection.y * 0.05));
            armLeftMotor1.setMaximumForce(20);
            armLeftMotor2.setMaximumForce(20);
            armLeftMotor1.setMaximumTorque(150);
            armLeftMotor2.setMaximumTorque(20);

            double rate = Math.abs(1 - (1 - punchDirection.distance(0, 0)) * 1.3);
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
        punchDirection.x -= punchDirection.x * dt * 15;
        punchDirection.y -= punchDirection.y * dt * 15;

    }

    public void cancelGrabbing() {
        if (grabbingObject instanceof GameObjectItem) {
            ((GameObjectItem) grabbingObject).setHoldPlayer(null);
            ((GameObjectItem) grabbingObject).forceUpdateModel(((GameObjectItem) grabbingObject).isFlipped());
        }
        head.getWorld().getSimulatedWorld().removeJoint(grabbingJoint);
        grabbingJoint = null;
        grabbingObject = null;
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


    public GameObjectItem getHoldItem() {
        if (this.grabbingJoint != null) {
            if (this.grabbingObject instanceof GameObjectItem) {
                return (GameObjectItem) grabbingObject;
            }
        }
        return null;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<GameObjectPlayerPart> getBodyParts() {
        return bodyParts;
    }

    public boolean isSneaking() {
        return sneak;
    }

    public Vector2 getReachDirection() {
        return reachDirection;
    }

    public Package createGrabbingSyncPackage() {
        Package pkg = new Package(PackageTypes.PLAYER_SYNC_GRAB_ITEM);
        pkg.setInteger("player", getID());
        pkg.setBoolean("grabbing", grabbingJoint != null);
        if (grabbingJoint != null) {
            pkg.setFraction("x", grabbingObject.getTransform().getTranslationX());
            pkg.setFraction("y", grabbingObject.getTransform().getTranslationY());
            pkg.setFraction("a", grabbingObject.getTransform().getRotationAngle());
            pkg.setInteger("object", grabbingObject.getObjectID());
        }
        return pkg;
    }

    public void syncGrabbingFromPackage(Package pkg) {
        if (pkg.getBoolean("grabbing")) {

            if (grabItemSynced) return;
            int objID = pkg.getInteger("object");
            if (grabbingJoint != null && objID != grabbingObject.getObjectID()) {

                cancelGrabbing();
            } else {
                Vector2 targetPos = new Vector2(pkg.getFraction("x"), pkg.getFraction("y"));
                double angle = pkg.getFraction("a");
                boolean needSync = false;
                GameObject grabbingObject = head.getWorld().getObject(pkg.getInteger("object"));
                if (targetPos.distanceSquared(grabbingObject.getTransform().getTranslation()) > 0.5) {
                    needSync = true;
                } else if ((angle - grabbingObject.getTransform().getRotationAngle() + Math.PI * 2) % (Math.PI * 2) > Math.PI / 45) {
                    needSync = true;
                }

                if (needSync) {
                    grabbingObject.getTransform().setTranslation(targetPos);
                    grabbingObject.getTransform().setRotation(angle);
                    if (grabbingJoint != null) {
                        head.getWorld().getSimulatedWorld().removeJoint(grabbingJoint);
                    }
                    tryGrabItem(grabbingObject, armRightEnd);
                }

            }
            tryGrabItem(head.getWorld().getObject(objID), armRightEnd);
            if (isGrabbing()) grabItemSynced = true;
        } else {
            if (grabbingJoint != null) {
                cancelGrabbing();
            }
            grabItemSynced = false;
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isGrabbing() {
        return grabbingObject != null;
    }

    public void moveTo(double x, double y) {
        for (GameObjectPlayerPart bodyPart : bodyParts) {
            double xOffset = bodyPart.getTransform().getTranslationX() - body.getTransform().getTranslationX();
            double yOffset = bodyPart.getTransform().getTranslationY() - body.getTransform().getTranslationY();
            bodyPart.getTransform().setTranslation(xOffset + x, yOffset + y);
            bodyPart.setLinearVelocity(0, 0);
        }
    }
}
