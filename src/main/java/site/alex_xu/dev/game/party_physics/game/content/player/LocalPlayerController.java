package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.util.LinkedList;

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

    @Override
    public void moveX(int x) {
        super.moveX(x);
        Package pkg = new Package(PackageTypes.PLAYER_SYNC_MOVEMENT_X);
        pkg.setInteger("player", getPlayer().getID());
        pkg.setInteger("x", getPlayer().getMovementX());
        send(pkg);
    }

    @Override
    public void sneak(boolean sneak) {
        super.sneak(sneak);
        Package pkg = new Package(PackageTypes.PLAYER_SYNC_SNEAK);
        pkg.setInteger("player", getPlayer().getID());
        pkg.setBoolean("sneak", getPlayer().isSneaking());
        send(pkg);
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
        super.reach(reach);
        Package pkg = new Package(PackageTypes.PLAYER_SYNC_REACH);
        pkg.setInteger("player", getPlayer().getID());
        pkg.setFraction("x", reach.x);
        pkg.setFraction("y", reach.y);
        send(pkg);
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
