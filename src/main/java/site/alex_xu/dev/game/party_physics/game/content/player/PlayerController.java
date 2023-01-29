package site.alex_xu.dev.game.party_physics.game.content.player;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.GameObjectItem;

/**
 * The parent class for all player controllers
 * (exist for multiplayer purpose)
 */
public abstract class PlayerController {
    private final Player player;

    private boolean isUsingItem = false;

    public PlayerController(Player player) {
        this.player = player;
    }

    /**
     * @return the player associated with this controller
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @param x x-offset
     */
    public void moveX(int x) {
        getPlayer().setMovementX(x);
    }

    /**
     * @param sneak true if should sneak otherwise false
     */
    public void sneak(boolean sneak) {
        getPlayer().setSneak(sneak);
    }

    /**
     * Try to make the player jump
     */
    public void jump() {
        getPlayer().jump();
    }

    /**
     * @param reach direction to reach
     */
    public void reach(Vector2 reach) {
        getPlayer().setReachDirection(reach);
    }

    /**
     * @param x x-component of the direction to reach
     * @param y y-component of the direction to reach
     */
    public void reach(double x, double y) {
        reach(new Vector2(x, y));
    }

    /**
     * @param punch the direction to punch
     */
    public void punch(Vector2 punch) {
        getPlayer().punch(punch);
    }

    /**
     * @param x x-component of the direction to punch
     * @param y y-component of the direction to punch
     */
    public void punch(double x, double y) {
        getPlayer().punch(new Vector2(x, y));
    }

    /**
     * @param use true if should use the holding item otherwise false
     */
    public void useItem(boolean use) {
        isUsingItem = use;
    }

    /**
     * @return true if is using item, other wise false
     */
    public boolean isUsingItem() {
        return isUsingItem;
    }

    /**
     * Update
     */
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
