package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectItem;

public abstract class PlayerController {
    private final Player player;

    private boolean isUsingItem = false;

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

    public void useItem(boolean use) {
        isUsingItem = use;
    }

    public boolean isUsingItem() {
        return isUsingItem;
    }

    public void tick() {
        if (isUsingItem) {
            if (getPlayer().getHoldItem() == null) {
                isUsingItem = false;
            } else {
                getPlayer().getHoldItem().use();
            }
        }
    }
}
