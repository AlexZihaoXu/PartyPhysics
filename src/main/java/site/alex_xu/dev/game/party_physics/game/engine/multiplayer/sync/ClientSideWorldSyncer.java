package site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync;

import site.alex_xu.dev.game.party_physics.game.content.player.NetworkPlayerController;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.Client;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.ClientEventHandler;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.JoiningClient;
import site.alex_xu.dev.game.party_physics.game.engine.networking.GameObjectManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.util.LinkedList;
import java.util.TreeMap;

public class ClientSideWorldSyncer implements ClientEventHandler {

    private final JoiningClient client;
    private GameWorld world = null;
    private final LinkedList<Package> sendQueue = new LinkedList<>();

    private final TreeMap<Integer, NetworkPlayerController> remoteControllers = new TreeMap<>();

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

        if (pkg.getType() == PackageTypes.WORLD_SYNC_CREATE) serverCreateWorld();
        else if (pkg.getType() == PackageTypes.WORLD_SYNC_ADD_OBJECT) serverAddObject(pkg);
        else if (pkg.getType() == PackageTypes.WORLD_SYNC_OBJECT_STATE) serverSyncObjectState(pkg);
        else if (pkg.getType() == PackageTypes.WORLD_SYNC_ADD_PLAYER) serverAddPlayer(pkg);
        else if (pkg.getType() == PackageTypes.WORLD_SYNC_REMOVE_PLAYER) serverRemovePlayer(pkg);
        else if (!(pkg.getType() == PackageTypes.PONG || pkg.getType() == PackageTypes.CLIENT_UPDATE_LATENCY)) {
            System.out.println(pkg);
            for (NetworkPlayerController controller : remoteControllers.values()) {
                controller.handlePackage(pkg);
            }
        }

    }

    private void serverSyncObjectState(Package pkg) {
        if (getWorld() != null)
            getWorld().syncObject(pkg);
    }


    // Server operations
    private void serverRemovePlayer(Package pkg) {
        if (getWorld() != null) {
            getWorld().removePlayer(getWorld().getPlayer(pkg.getInteger("id")));
        }
    }

    private void serverCreateWorld() {
        world = new GameWorld();
        world.init();
    }

    private void serverAddObject(Package pkg) {
        GameObject obj = GameObjectManager.getInstance().createFromPackage(pkg);
        getWorld().addObject(obj);
    }

    private void serverAddPlayer(Package pkg) {
        getWorld().addPlayer(GameObjectManager.getInstance().createPlayerFromPackage(pkg));
    }

    @Override
    public void onClientJoin(Client client) {
        if (client.getID() != getClient().getOwnClient().getID()) {
            NetworkPlayerController controller = new NetworkPlayerController(getWorld().getPlayer(client.getID()));
            remoteControllers.put(client.getID(), controller);
        }
    }

    @Override
    public void onClientLeave(Client client) {
        if (client.getID() != getClient().getOwnClient().getID()) {
            remoteControllers.remove(client.getID());
        }
    }
}
