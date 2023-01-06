package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.TreeSet;

public class LocalPlayerController extends PlayerController {

    private final LinkedList<Package> sendQueue = new LinkedList<>();

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
        }
    }

    public void onKeyReleased(int keyCode) {
        pressedKeys.remove(keyCode);
        if (keyCode == KeyEvent.VK_S) {
            sneak(false);
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
}
