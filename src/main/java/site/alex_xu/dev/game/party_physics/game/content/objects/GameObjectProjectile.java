package site.alex_xu.dev.game.party_physics.game.content.objects;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerHead;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public abstract class GameObjectProjectile extends GameObject {

    public GameObjectProjectile() {
        super();
        getRenderPos().set(100000, 100000);
    }

    @Override
    public void onRender(Renderer renderer) {

    }

    public void onHit(GameObject object, Vector2 location) {
    }

    public abstract boolean shouldDelete();
}
