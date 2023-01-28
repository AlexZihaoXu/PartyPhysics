package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

/**
 * Client class
 * (ABSTRACTED CLIENT, NO SOCKET IS IN THIS CLIENT)
 */
public class Client {
    private final int id;
    private final String name;

    double latency = 0;

    /**
     * @param id the ID assigned to the client
     * @param name the name of the client
     */
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

    /**
     * @return the ID
     */
    public int getID() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return a package that contains information about client joining
     */
    Package createJoinPackage() {
        Package pkg = new Package(PackageTypes.CLIENT_JOIN);
        pkg.setInteger("id", id);
        pkg.setString("name", name);
        return pkg;
    }

    /**
     * @return a package that contains information about client quitting
     */
    Package createLeavePackage() {
        Package pkg = new Package(PackageTypes.CLIENT_LEAVE);
        pkg.setInteger("id", id);
        pkg.setString("name", name);
        return pkg;
    }
}
