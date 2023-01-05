package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

public class Client {
    private final int id;
    private final String name;

    double latency = 0;

    Client(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @return returns latency in milliseconds
     */
    public double getLatency() {
        return latency;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    Package createJoinPackage() {
        Package pkg = new Package(PackageTypes.CLIENT_JOIN);
        pkg.setInteger("id", id);
        pkg.setString("name", name);
        return pkg;
    }

    Package createLeavePackage() {
        Package pkg = new Package(PackageTypes.CLIENT_LEAVE);
        pkg.setInteger("id", id);
        pkg.setString("name", name);
        return pkg;
    }
}
