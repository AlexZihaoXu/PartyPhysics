package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Camera;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.TreeSet;

public class LocalPlayerController extends PlayerController {

    private final LinkedList<Package> sendQueue = new LinkedList<>();

    private Camera camera = new Camera();

    private boolean isHoldingItem = false;
    private boolean itemUseLocked = false;

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public LocalPlayerController(Player player) {
        super(player);
    }

    private void send(Package pkg) {
        sendQueue.addLast(pkg);
    }

    public Package pull() {
        if (sendQueue.isEmpty()) return null;
        return sendQueue.removeFirst();
    }

    private final TreeSet<Integer> pressedKeys = new TreeSet<>();

    public void onKeyPressed(int keyCode) {
        pressedKeys.add(keyCode);
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_SPACE) {
            jump();
        } else if (keyCode == KeyEvent.VK_S) {
            sneak(true);
        } else if (keyCode == KeyEvent.VK_F) {
            if (getPlayer().getHoldItem() != null) {
                reach(0, 0);
            }
        }
    }

    public void onKeyReleased(int keyCode) {
        pressedKeys.remove(keyCode);
        if (keyCode == KeyEvent.VK_S) {
            sneak(false);
        }
    }

    public void onMousePressed(double x, double y, int button) {
        if (button == 3) {
            Vector2 mouse = camera.getWorldMousePos();
            Vector2 playerPos = getPlayer().getPos().copy().add(0, -0.3);
            punch(Vector2.create(-1, playerPos.subtract(mouse).getDirection() - getPlayer().body.getTransform().getRotationAngle()));
        }
        if (button == 1) {
            itemUseLocked = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        int mx = 0;
        if (pressedKeys.contains(KeyEvent.VK_A))
            mx--;
        if (pressedKeys.contains(KeyEvent.VK_D))
            mx++;
        moveX(mx);

        Vector2 mouse = camera.getWorldMousePos();
        Vector2 playerPos = getPlayer().getPos().copy().add(0, -0.3);
        if (PartyPhysicsWindow.getInstance().getMouseButton(1)) {
            if (mouse.distanceSquared(playerPos) > 1) {
                reach(Vector2.create(-1, playerPos.subtract(mouse).getDirection() - getPlayer().body.getTransform().getRotationAngle()));
            }
        } else {
            if (getPlayer().getHoldItem() == null) {
                reach(0, 0);
            } else {
                reach(Vector2.create(-1, playerPos.subtract(mouse).getDirection() - getPlayer().body.getTransform().getRotationAngle()));
            }
        }

        if (getPlayer().getHoldItem() == null) {
            isHoldingItem = false;
        } else {
            if (!isHoldingItem) {
                isHoldingItem = true;
                itemUseLocked = true;
            }
        }

        if (!itemUseLocked){
            useItem(PartyPhysicsWindow.getInstance().getMouseButton(1));
        }

    }

    @Override
    public void useItem(boolean use) {
        if (getPlayer().getHoldItem() == null) return;
        if (use != isUsingItem()) {
            super.useItem(use);
            Package pkg = new Package(PackageTypes.PLAYER_SYNC_USE_ITEM);
            pkg.setInteger("player", getPlayer().getID());
            pkg.setBoolean("use", use);
            send(pkg);
        }
    }

    @Override
    public void moveX(int x) {
        if (x != getPlayer().getMovementX()) {
            super.moveX(x);
            Package pkg = new Package(PackageTypes.PLAYER_SYNC_MOVEMENT_X);
            pkg.setInteger("player", getPlayer().getID());
            pkg.setInteger("x", getPlayer().getMovementX());
            send(pkg);
        }
    }

    @Override
    public void sneak(boolean sneak) {
        if (sneak != getPlayer().isSneaking()) {
            super.sneak(sneak);
            Package pkg = new Package(PackageTypes.PLAYER_SYNC_SNEAK);
            pkg.setInteger("player", getPlayer().getID());
            pkg.setBoolean("sneak", getPlayer().isSneaking());
            send(pkg);
        }
    }

    @Override
    public void jump() {
        super.jump();
        Package pkg = new Package(PackageTypes.PLAYER_SYNC_JUMP);
        pkg.setInteger("player", getPlayer().getID());
        send(pkg);
    }

    @Override
    public void reach(Vector2 reach) {

        double magDiff = Math.abs(reach.getMagnitude() - getPlayer().getReachDirection().getMagnitude());
        double dirDiff = Math.abs(reach.getDirection() - getPlayer().getReachDirection().getDirection());

        if (magDiff > 0.1 || dirDiff > Math.PI / 90) {
            super.reach(reach);
            Package pkg = new Package(PackageTypes.PLAYER_SYNC_REACH);
            pkg.setInteger("player", getPlayer().getID());
            pkg.setFraction("x", reach.x);
            pkg.setFraction("y", reach.y);
            send(pkg);
        }

    }

    @Override
    public void punch(Vector2 punch) {
        super.punch(punch);
        Package pkg = new Package(PackageTypes.PLAYER_SYNC_PUNCH);
        pkg.setInteger("player", getPlayer().getID());
        pkg.setFraction("x", punch.x);
        pkg.setFraction("y", punch.y);
        send(pkg);
    }

    public Camera getCamera() {
        return camera;
    }
}
