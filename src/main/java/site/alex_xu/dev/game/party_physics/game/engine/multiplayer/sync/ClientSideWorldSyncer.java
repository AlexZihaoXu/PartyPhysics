package site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync;

import site.alex_xu.dev.game.party_physics.game.content.player.LocalPlayerController;
import site.alex_xu.dev.game.party_physics.game.content.player.NetworkPlayerController;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.Client;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.ClientEventHandler;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.JoiningClient;
import site.alex_xu.dev.game.party_physics.game.engine.networking.GameObjectManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;

import java.util.LinkedList;
import java.util.TreeMap;

public class ClientSideWorldSyncer implements ClientEventHandler {

    private final JoiningClient client;
    private GameWorld world = null;
    private final LinkedList<Package> sendQueue = new LinkedList<>();

    private final TreeMap<Integer, NetworkPlayerController> remoteControllers = new TreeMap<>();

    private LocalPlayerController controller;

    public ClientSideWorldSyncer(JoiningClient client) {
        this.client = client;
        GameObject.serverSideWorldSyncer = null;
    }

    public GameWorld getWorld() {
        return world;
    }

    public JoiningClient getClient() {
        return client;
    }

    public void send(Package pkg) {
        sendQueue.addLast(pkg);
    }

    public void tick() {
        if (world != null) {
            world.onTick();
        }
        GameObject.latency = client.getOwnClient().getLatency() / 1000;
        while (!sendQueue.isEmpty()) {
            client.send(sendQueue.removeFirst());
        }

        for (NetworkPlayerController controller : remoteControllers.values()) {
            controller.tick();
        }

        if (controller != null) controller.tick();
    }

    public void handlePackage(Package pkg) {

        if (pkg.getType() == PackageTypes.WORLD_SYNC_CREATE) serverCreateWorld();
        else if (pkg.getType() == PackageTypes.WORLD_SYNC_ADD_OBJECT) serverAddObject(pkg);
        else if (pkg.getType() == PackageTypes.WORLD_SYNC_OBJECT_STATE) serverSyncObjectState(pkg);
        else if (pkg.getType() == PackageTypes.WORLD_SYNC_ADD_PLAYER) serverAddPlayer(pkg);
        else if (pkg.getType() == PackageTypes.WORLD_SYNC_REMOVE_PLAYER) serverRemovePlayer(pkg);
        else if (pkg.getType() == PackageTypes.PLAYER_SYNC_GRAB_ITEM) {
            world.getPlayer(pkg.getInteger("player")).syncGrabbingFromPackage(pkg);
        } else if (pkg.getType() == PackageTypes.CAMERA_ADD_SHAKE) {
            if (getLocalController() != null) {
                getLocalController().getCamera().addShake(
                        pkg.getFraction("mag"),
                        pkg.getFraction("dir"),
                        pkg.getFraction("speed"),
                        pkg.getBoolean("gun")
                );
            }
        } else if (pkg.getType() == PackageTypes.PLAYER_SYNC_HEALTH_UPDATE) {
            serverUpdatePlayerHealth(pkg);
        } else if (pkg.getType() == PackageTypes.WORLD_SYNC_REMOVE_OBJECT) {
            world.removeObject(world.getObject(pkg.getInteger("id")));
        } else if (pkg.getType() == PackageTypes.SOUND_PLAY) {
            SoundSystem.getInstance().getGameSourceGroup2().setVelocity(0, 0, 0);
            SoundSystem.getInstance().getGameSourceGroup2().setLocation(pkg.getFraction("x"), pkg.getFraction("y"), 0);
            SoundSystem.getInstance().getGameSourceGroup2().play(pkg.getString("path"));
        } else {
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

    private void serverUpdatePlayerHealth(Package pkg) {
        if (getWorld() != null) {
            getWorld().getPlayer(pkg.getInteger("id")).setHealth(pkg.getFraction("hp"));
        }
    }

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
        if (getWorld().hasPlayer(pkg.getInteger("playerID"))) return;
        Player player = GameObjectManager.getInstance().createPlayerFromPackage(pkg);
        getWorld().addPlayer(player);
    }

    @Override
    public void onClientJoin(Client client) {
        if (client.getID() == getClient().getOwnClient().getID()) {
            Player player = getWorld().getPlayer(client.getID());
            controller = new LocalPlayerController(player);
            player.setDisplayName(client.getName());
        } else {
            Player player = getWorld().getPlayer(client.getID());
            NetworkPlayerController controller = new NetworkPlayerController(player);
            remoteControllers.put(client.getID(), controller);
            player.setDisplayName(client.getName());
        }
    }

    @Override
    public void onClientLeave(Client client) {
        if (client.getID() != getClient().getOwnClient().getID()) {
            remoteControllers.remove(client.getID());
        }
    }

    public LocalPlayerController getLocalController() {
        return controller;
    }
}
