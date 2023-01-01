package site.alex_xu.dev.game.party_physics.game.engine.framework;

import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectBox;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectGround;
import site.alex_xu.dev.game.party_physics.game.content.objects.map.GameObjectWoodPlank;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.ServerManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.GameObjectManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.engine.physics.PhysicsSettings;

import java.awt.*;
import java.util.LinkedList;

public class GameWorldServerManager {
    private final GameWorld world;
    private final ServerManager server;
    private final LinkedList<Package> packagesQueue = new LinkedList<>();

    private double lastSyncTime = 0;

    public GameWorldServerManager(GameWorld world, ServerManager serverManager) {
        this.world = world;
        this.server = serverManager;
    }

    public GameWorldServerManager() {
        this(new GameWorld(), new ServerManager());
        getWorld().init();
    }

    private void processPackages() {
        while (true) {
            Package pkg = getServer().pull();
            if (pkg == null) {
                break;
            } else {
                if (pkg.getType() == PackageTypes.HANDSHAKE) {
                    System.out.println("client request handshake");
                    for (GameObject object : getWorld().getObjects()) {
                        getServer().getClient(pkg).send(GameObjectManager.getInstance().createCreationPackage(object));
                    }
                } else {
                    packagesQueue.addLast(pkg);
                }
            }
        }
    }

    public void onTick() {
        getWorld().onTick();
        if (getWorld().getCurrentTime() - lastSyncTime > 1d / PhysicsSettings.SYNCS_PER_SECOND) {
            for (GameObject object : getWorld().getObjects()) {
                server.broadCast(object.createSyncPackage());
            }
            lastSyncTime = getWorld().getCurrentTime();
        }
        processPackages();
        getServer().flush();

    }

    public void load() {
        getServer().start();
    }

    public void offload() {
        getServer().close();
    }

    public Package pull() {
        if (packagesQueue.isEmpty()) {
            return null;
        }
        return packagesQueue.removeFirst();
    }

    public GameWorld getWorld() {
        return world;
    }

    public ServerManager getServer() {
        return server;
    }

    public GameObjectBox createBox(double x, double y) {

        GameObjectBox box = new GameObjectBox(x, y);
        getWorld().addObject(box);

        Package pkg = GameObjectManager.getInstance().createCreationPackage(box);
        getServer().broadCast(pkg);

        return box;
    }

    public GameObjectGround createGround(double x, double y, double w, double h) {

        GameObjectGround ground = new GameObjectGround(x, y, w, h);
        getWorld().addObject(ground);

        Package pkg = GameObjectManager.getInstance().createCreationPackage(ground);
        getServer().broadCast(pkg);

        return ground;
    }

    public GameObjectWoodPlank createWoodPlank(double x, double y, double w, double h) {
        GameObjectWoodPlank woodPlank = new GameObjectWoodPlank(x, y, w, h);
        getWorld().addObject(woodPlank);

        Package pkg = GameObjectManager.getInstance().createCreationPackage(woodPlank);
        getServer().broadCast(pkg);

        return woodPlank;
    }

    public Player createPlayer(double x, double y, Color color) {

        Player player = new Player(color, x, y);
        getWorld().addPlayer(player);

        Package pkg = new Package(PackageTypes.PHYSICS_SYNC_GAME_PLAYER_CREATE);
        int colorInt = (((color.getRed() * 256) + color.getGreen()) * 256 + color.getBlue()) * 256 + color.getAlpha();
        pkg.setInteger("color", colorInt);
        pkg.setFraction("x", x);
        pkg.setFraction("y", y);
        getServer().broadCast(pkg);

        return player;
    }

}
