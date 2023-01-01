package site.alex_xu.dev.game.party_physics.game.engine.framework;

import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.ClientManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.GameObjectManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.awt.*;
import java.util.LinkedList;

public class GameWorldClientManager {
    private final GameWorld world;
    private final ClientManager client;

    private final LinkedList<Package> packagesQueue = new LinkedList<>();

    public GameWorldClientManager(GameWorld world, ClientManager clientManager) {
        this.world = world;
        this.client = clientManager;
    }

    public GameWorldClientManager() {
        this(new GameWorld(), new ClientManager());
        getWorld().init();
    }

    public void load() {
        getClient().start();
        getClient().send(new Package(PackageTypes.HANDSHAKE));
    }

    public void offload() {
        getClient().close();
    }

    public GameWorld getWorld() {
        return world;
    }

    public ClientManager getClient() {
        return client;
    }

    private void processPackages() {
        while (true) {
            Package pkg = getClient().pull();
            if (pkg == null) {
                break;
            } else {
                if (pkg.getType() == PackageTypes.PHYSICS_SYNC_GAME_OBJECT_CREATE) {
                    GameObject obj = GameObjectManager.getInstance().createFromPackage(pkg);
                    getWorld().addObject(obj);
                } else if (pkg.getType() == PackageTypes.PHYSICS_SYNC_GAME_OBJECT_TRANSFORM) {
                    getWorld().syncObject(pkg);
                } else if (pkg.getType() == PackageTypes.PHYSICS_SYNC_GAME_PLAYER_CREATE) {
                    getWorld().addPlayer(GameObjectManager.getInstance().createPlayerFromPackage(pkg));
                } else if (pkg.getType() == PackageTypes.GAME_PLAYER_MOVEMENT_X_SET) {
                    getWorld().getPlayer(pkg.getInteger("id")).setMovementX(pkg.getInteger("x"));
                } else {
                    packagesQueue.addLast(pkg);
                }
            }
        }
    }

    public void onTick() {
        getWorld().onTick();
        processPackages();
        getClient().flush();
    }

    public Package pull() {
        if (packagesQueue.isEmpty()) {
            return null;
        }
        return packagesQueue.removeFirst();
    }
}
