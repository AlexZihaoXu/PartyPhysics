package site.alex_xu.dev.game.party_physics.game.content.objects;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class GameObjectProjectile extends GameObject {

    public GameObjectProjectile(){
        super();
        getRenderPos().set(100000, 100000);
    }

    @Override
    public void onRender(Renderer renderer) {

    }

    public boolean onHit(GameObject object, Vector2 location) {
        return false;
    }
}
