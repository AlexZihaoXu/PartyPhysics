package site.alex_xu.dev.game.party_physics.game.content.objects;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerHead;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

/**
 * The parent class for all projectiles
 */
public abstract class GameObjectProjectile extends GameObject {

    public GameObjectProjectile() {
        super();
        getRenderPos().set(100000, 100000);
    }

    @Override
    public void onRender(Renderer renderer) {

    }

    /**
     * @param object the hit object
     * @param location the contact location
     */
    public void onHit(GameObject object, Vector2 location) {
    }

    /**
     * @return true if this particle should be deleted
     */
    public abstract boolean shouldDelete();
}
