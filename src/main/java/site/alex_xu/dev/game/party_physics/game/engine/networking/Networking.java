package site.alex_xu.dev.game.party_physics.game.engine.networking;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.items.GameObjectItemPistol;
import site.alex_xu.dev.game.party_physics.game.content.objects.items.GameObjectItemSMG;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectTNT;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectWoodPlank;
import site.alex_xu.dev.game.party_physics.game.content.objects.projectile.GameObjectLiteBullet;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerBody;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerFoot;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerHead;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerLimb;

/**
 * A class that handles initializing stuff and make them ready for communicating over network
 */
public class Networking {
    /**
     * The only instance of the Networking class
     */
    private static Networking INSTANCE = null;

    /**
     * @return the only instance (Singleton)
     */
    public static Networking getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Networking();
        }
        return INSTANCE;
    }

    public void init() {
        initGameObjectList();
    }

    /**
     * Register all object to the object manager for serialization purpose
     */
    public void initGameObjectList() {
        GameObjectManager manager = GameObjectManager.getInstance();

        manager.register(new GameObjectBox(0, 0));
        manager.register(new GameObjectGround(0, 0, 1, 1));
        manager.register(new GameObjectWoodPlank(0, 0, 1, 1));
        manager.register(new GameObjectItemPistol(0, 0));
        manager.register(new GameObjectItemSMG(0, 0));
        manager.register(new GameObjectLiteBullet(new Vector2(), new Vector2()));
        manager.register(new GameObjectPlayerHead(0, 0));
        manager.register(new GameObjectPlayerLimb(1, 1));
        manager.register(new GameObjectPlayerBody(0, 0));
        manager.register(new GameObjectPlayerFoot(0, 0));
        manager.register(new GameObjectTNT(0, 0));

    }
}
