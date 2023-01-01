package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.ClientManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.GameObjectManager;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.util.LinkedList;

public class GameWorldClientManager {
    private final GameWorld world;
    private final ClientManager client;

    private final LinkedList<Package> packagesQueue = new LinkedList<>();

    private final LinkedList<Double> packageRXPackageSpeedCountList = new LinkedList<>();
    private final LinkedList<Integer> packageRXBytesSpeedCountList = new LinkedList<>();

    private int packageRXBytesSpeedSum = 0;
    private final LinkedList<Double> packageTXSpeedCountList = new LinkedList<>();

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
        double now = Clock.currentTime();
        while (true) {
            Package pkg = getClient().pull();
            if (pkg == null) {
                break;
            } else {
                packageRXPackageSpeedCountList.add(now);
                packageRXBytesSpeedCountList.add(pkg.getPackageSize());
                packageRXBytesSpeedSum += pkg.getPackageSize();
                if (pkg.getType() == PackageTypes.PHYSICS_SYNC_GAME_OBJECT_CREATE) {
                    GameObject obj = GameObjectManager.getInstance().createFromPackage(pkg);
                    getWorld().addObject(obj);
                } else if (pkg.getType() == PackageTypes.PHYSICS_SYNC_GAME_OBJECT_TRANSFORM) {
                    getWorld().syncObject(pkg);
                } else if (pkg.getType() == PackageTypes.PHYSICS_SYNC_GAME_PLAYER_CREATE) {
                    getWorld().addPlayer(GameObjectManager.getInstance().createPlayerFromPackage(pkg));
                } else if (pkg.getType() == PackageTypes.GAME_PLAYER_MOVEMENT_X_SET) {
                    getWorld().getPlayer(pkg.getInteger("id")).setMovementX(pkg.getInteger("x"));
                } else if (pkg.getType() == PackageTypes.GAME_PLAYER_MOVEMENT_JUMP) {
                    getWorld().getPlayer(pkg.getInteger("id")).jump();
                } else if (pkg.getType() == PackageTypes.GAME_PLAYER_MOVEMENT_SNEAK) {
                    getWorld().getPlayer(pkg.getInteger("id")).setSneak(pkg.getBoolean("sneak"));
                } else if (pkg.getType() == PackageTypes.GAME_PLAYER_REACH_DIRECTION_SET) {
                    getWorld().getPlayer(pkg.getInteger("id")).setReachDirection(
                            new Vector2(pkg.getFraction("x"), pkg.getFraction("y"))
                    );
                } else if (pkg.getType() == PackageTypes.PHYSICS_SYNC_GAME_PLAYER_GRAB) {
                    getWorld().getPlayer(pkg.getInteger("player")).syncGrabbingFromPackage(pkg);
                } else {
                    packagesQueue.addLast(pkg);
                }
            }
        }

        while (!packageRXPackageSpeedCountList.isEmpty() && now - packageRXPackageSpeedCountList.getFirst() > 1) {
            packageRXPackageSpeedCountList.removeFirst();
            packageRXBytesSpeedSum -= packageRXBytesSpeedCountList.removeFirst();
        }

    }

    public int getRXPKGSpeed() {
        return packageRXPackageSpeedCountList.size();
    }

    public int getRXBytesSpeed() {
        return packageRXBytesSpeedSum;
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
