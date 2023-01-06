package site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync;

import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.Client;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.ClientEventHandler;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.HostingClient;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.HostingServer;
import site.alex_xu.dev.game.party_physics.game.engine.networking.GameObjectManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.util.LinkedList;

public class ServerSideWorldSyncer implements ClientEventHandler {
    private final HostingServer server;
    private GameWorld world = null;

    private final LinkedList<Package> sendQueue = new LinkedList<>();

    private final Clock forceSyncClock = new Clock();

    public ServerSideWorldSyncer(HostingServer server) {
        this.server = server;
    }

    public HostingServer getServer() {
        return server;
    }

    public void broadcast(Package pkg) {
        pkg.setBoolean("sync", true);
        sendQueue.addLast(pkg);
    }

    public void tick() {
        if (world != null) {
            world.onTick();

            if (forceSyncClock.elapsedTime() > 1d / PhysicsSettings.SYNCS_PER_SECOND) {

                for (GameObject object : world.getObjects()) {
                    if (!object.isAtRest())
                        broadcast(object.createSyncPackage());
                }

                forceSyncClock.reset();
            }

        }

        while (!sendQueue.isEmpty()) {
            server.broadcast(sendQueue.removeFirst());
        }
    }

    public void handlePackage(Package pkg) {
        if (pkg.hasKey("sync")) {


        }
    }

    public GameWorld getWorld() {
        if (world == null) {
            syncCreateWorld();
        }
        return world;
    }

    public void initializeClient(HostingClient client) {
        if (world != null) {
            Package pkg = new Package(PackageTypes.WORLD_SYNC_CREATE);
            pkg.setBoolean("sync", true);
            client.send(pkg);
            for (GameObject object : world.getObjects()) {
                pkg = GameObjectManager.getInstance().createCreationPackage(object);
                pkg.setBoolean("sync", true);
                client.send(pkg);
            }
        }
    }

    public void syncCreateWorld() {
        world = new GameWorld();
        world.init();
        Package pkg = new Package(PackageTypes.WORLD_SYNC_CREATE);
        broadcast(pkg);
    }

    public void syncAddBox(double x, double y) {
        GameWorld world = getWorld();
        GameObjectBox box = new GameObjectBox(x, y);
        Package pkg = GameObjectManager.getInstance().createCreationPackage(box);
        broadcast(pkg);
        world.addObject(box);
    }

    public void syncAddGround(double x, double y, double w, double h) {
        GameWorld world = getWorld();
        GameObject box = new GameObjectGround(x, y, w, h);
        Package pkg = GameObjectManager.getInstance().createCreationPackage(box);
        broadcast(pkg);
        world.addObject(box);
    }

    @Override
    public void onClientJoin(Client client) {
        initializeClient(server.getHostingClient(client));
    }

    @Override
    public void onClientLeave(Client client) {

    }
}
