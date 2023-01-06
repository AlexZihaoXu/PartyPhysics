package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.geometry.Vector2;

public abstract class PlayerController {
    private final Player player;

    public PlayerController(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void moveX(int x) {
        getPlayer().setMovementX(x);
    }

    public void sneak(boolean sneak) {
        getPlayer().setSneak(sneak);
    }

    public void jump() {
        getPlayer().jump();
    }

    public void reach(Vector2 reach) {
        getPlayer().setReachDirection(reach);
    }

    public void reach(double x, double y) {
        reach(new Vector2(x, y));
    }

    public void punch(Vector2 punch) {
        getPlayer().punch(punch);
    }

    public void punch(double x, double y) {
        getPlayer().punch(new Vector2(x, y));
    }

    public void tick() {

    }
}
