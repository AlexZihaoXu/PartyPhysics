package site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync;

import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.JoiningClient;
import site.alex_xu.dev.game.party_physics.game.engine.networking.GameObjectManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.util.LinkedList;

public class ClientSideWorldSyncer {

    private final JoiningClient client;
    private GameWorld world = null;
    private final LinkedList<Package> sendQueue = new LinkedList<>();

    public ClientSideWorldSyncer(JoiningClient client) {
        this.client = client;
    }

    public GameWorld getWorld() {
        return world;
    }

    public JoiningClient getClient() {
        return client;
    }

    public void send(Package pkg) {
        pkg.setBoolean("sync", true);
        sendQueue.addLast(pkg);
    }

    public void tick() {
        if (world != null) {
            world.onTick();
        }
        while (!sendQueue.isEmpty()) {
            client.send(sendQueue.removeFirst());
        }
    }

    public void handlePackage(Package pkg) {
        if (pkg.hasKey("sync")) {
            if (pkg.getType() == PackageTypes.WORLD_SYNC_CREATE) serverCreateWorld();
            if (pkg.getType() == PackageTypes.WORLD_SYNC_ADD_OBJECT) serverAddObject(pkg);
            if (pkg.getType() == PackageTypes.WORLD_SYNC_OBJECT_STATE) serverSyncObjectState(pkg);

        }
    }

    private void serverSyncObjectState(Package pkg) {
        getWorld().syncObject(pkg);
    }

    // Server operations

    private void serverCreateWorld() {
        world = new GameWorld();
        world.init();
    }

    private void serverAddObject(Package pkg) {
        GameObject obj = GameObjectManager.getInstance().createFromPackage(pkg);
        getWorld().addObject(obj);
    }
}
