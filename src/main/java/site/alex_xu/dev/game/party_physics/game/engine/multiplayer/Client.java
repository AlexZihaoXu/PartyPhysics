package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.io.IOException;

public class Client implements Runnable {

    private final ClientSocket socket;
    private final ServerManager server;

    final Thread thread;

    private final int clientID;

    Client(int clientID, ServerManager server, ClientSocket socket) {
        this.server = server;
        this.socket = socket;
        this.clientID = clientID;
        thread = new Thread(this, "Thread-Client(id=" + clientID + ")");
    }

    public int getClientID() {
        return clientID;
    }

    public void send(Package pkg) {
        socket.send(pkg);
    }

    @Override
    public void run() {
        try {
            while (!server.socket.isClosed() && !socket.isClosed()) {
                Package pkg = socket.pull();
                pkg.setInteger("clientID", clientID);
                synchronized (server.packagesQueue) {
                    server.packagesQueue.addLast(pkg);
                }
                Thread.yield();
            }
        } catch (IOException ignored) {
        } finally {
            Package pkg = new Package(PackageTypes.CONNECTION_LOST);
            pkg.setInteger("clientID", clientID);
            synchronized (server.packagesQueue) {
                server.packagesQueue.addLast(pkg);
            }
            server.clientsMap.remove(this.clientID);
            socket.close();
        }
    }

    public void flush() {
        socket.flush();
    }
}
