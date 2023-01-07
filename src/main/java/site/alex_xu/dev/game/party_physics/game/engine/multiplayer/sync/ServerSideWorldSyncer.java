package site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.player.GameObjectPlayerPart;
import site.alex_xu.dev.game.party_physics.game.content.player.LocalPlayerController;
import site.alex_xu.dev.game.party_physics.game.content.player.NetworkPlayerController;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
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

import java.awt.*;
import java.util.*;

public class ServerSideWorldSyncer implements ClientEventHandler {

    private static class ObjectState {
        public Vector2 pos = new Vector2();
        public Vector2 vel = new Vector2();
        public double angle;
        public double angularVel;

        public ObjectState(GameObject object) {
            pos.set(object.getTransform().getTranslation());
            vel.set(object.getLinearVelocity());
            angle = object.getTransform().getRotationAngle();
            angularVel = object.getAngularVelocity();
        }

        public boolean checkUpdate(GameObject object) {
            boolean shouldUpdate = false;

            double maxOffset = 0.02;
            double velOffset = 0.05;
            double angleOffset = Math.PI / 180;
            double angleVelOffset = Math.PI / 90;
            if (object.getTransform().getTranslation().copy().subtract(pos).getMagnitude() > maxOffset)
                shouldUpdate = true;
            else if (object.getLinearVelocity().copy().subtract(vel).getMagnitude() > velOffset)
                shouldUpdate = true;
            else if (Math.abs(angle - object.getTransform().getRotationAngle()) > angleOffset)
                shouldUpdate = true;
            else if (Math.abs(angularVel - object.getAngularVelocity()) > angleVelOffset)
                shouldUpdate = true;

            if (shouldUpdate) {
                pos.set(object.getTransform().getTranslation());
                vel.set(object.getLinearVelocity());
                angle = object.getTransform().getRotationAngle();
                angularVel = object.getAngularVelocity();
            }
            return shouldUpdate;
        }
    }

    private final TreeMap<Integer, ObjectState> objectStates = new TreeMap<>();

    private final HostingServer server;
    private GameWorld world = null;

    private final TreeMap<Integer, NetworkPlayerController> remoteControllers = new TreeMap<>();
    private final ArrayList<Color> randomColors = new ArrayList<>();
    private final LinkedList<Package> sendQueue = new LinkedList<>();

    private final Clock forceSyncClock = new Clock();

    private LocalPlayerController localPlayerController;

    public ServerSideWorldSyncer(HostingServer server) {
        this.server = server;
        randomColors.add(new Color(190, 51, 51));
        randomColors.add(new Color(231, 185, 34));
        randomColors.add(new Color(118, 169, 83));
        randomColors.add(new Color(77, 190, 174));
        randomColors.add(new Color(65, 154, 218));
        randomColors.add(new Color(132, 77, 218));
        randomColors.add(new Color(187, 65, 192));
    }

    public HostingServer getServer() {
        return server;
    }

    public void broadcast(Package pkg) {
        sendQueue.addLast(pkg);
    }

    public void tick() {
        if (world != null) {
            GameObject.latency = 0;
            world.onTick();
            if (forceSyncClock.elapsedTime() > 1d / PhysicsSettings.SYNCS_PER_SECOND) {
                HashSet<GameObject> updated = new HashSet<>();

                ArrayList<Integer> removed = new ArrayList<>();
                for (int id : objectStates.keySet()) {
                    if (!world.hasObject(id)) {
                        removed.add(id);
                    }
                }
                for (int id : removed) {
                    objectStates.remove(id);
                }

                for (GameObject object : world.getObjects()) {
                    if (!objectStates.containsKey(object.getObjectID()))
                        updated.add(object);
                }

                for (int id : objectStates.keySet()) {
                    ObjectState state = objectStates.get(id);
                    if (state.checkUpdate(world.getObject(id)) || world.getObject(id) instanceof GameObjectPlayerPart) {
                        updated.add(world.getObject(id));
                    }
                }

                for (GameObject object : updated) {
                    broadcast(object.createSyncPackage());
                    if (!objectStates.containsKey(object.getObjectID())) {
                        objectStates.put(object.getObjectID(), new ObjectState(object));
                    }
                }

                forceSyncClock.reset();
            }

        }

        if (getLocalPlayerController() != null) {
            LocalPlayerController controller = getLocalPlayerController();
            while (true) {
                Package pkg = controller.pull();
                if (pkg == null) break;
                server.broadcast(pkg);
            }
        }

        while (!sendQueue.isEmpty()) {
            server.broadcast(sendQueue.removeFirst());
        }
    }

    public LocalPlayerController getLocalPlayerController() {
        return localPlayerController;
    }

    public void handlePackage(Package pkg) {
        for (NetworkPlayerController controller : remoteControllers.values()) {
            controller.handlePackage(pkg);
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
            client.send(pkg);
            for (GameObject object : world.getObjects()) {
                if (object instanceof GameObjectPlayerPart)
                    continue;
                pkg = GameObjectManager.getInstance().createCreationPackage(object);
                client.send(pkg);
            }
            for (Player player : world.getPlayers()) {
                Package ppkg = GameObjectManager.getInstance().createCreationPackage(player);
                client.send(ppkg);
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

    public void syncAddPlayer(double x, double y, Color color, Client client) {
        GameWorld world = getWorld();
        Player player = new Player(color, x, y, client.getID());
        world.addPlayer(player);

        Package pkg = GameObjectManager.getInstance().createCreationPackage(player);
        broadcast(pkg);
    }

    public void syncRemovePlayer(Player player) {
        GameWorld world = getWorld();
        world.removePlayer(player);
        Package pkg = new Package(PackageTypes.WORLD_SYNC_REMOVE_PLAYER);
        pkg.setInteger("id", player.getID());
        randomColors.add(player.getColor());
        broadcast(pkg);
    }

    @Override
    public void onClientJoin(Client client) {

        int index = (int) (Math.random() * randomColors.size());
        Color color = randomColors.remove(index);
        syncAddPlayer(index / 5d, -2, color, client);

        if (client.getID() != 0) {
            initializeClient(server.getHostingClient(client));
            NetworkPlayerController controller = new NetworkPlayerController(getWorld().getPlayer(client.getID()));
            remoteControllers.put(client.getID(), controller);
        }

        if (client.getID() == 0) {
            localPlayerController = new LocalPlayerController(getWorld().getPlayer(0));
        }
    }

    @Override
    public void onClientLeave(Client client) {
        syncRemovePlayer(getWorld().getPlayer(client.getID()));

        remoteControllers.remove(client.getID());
    }
}
