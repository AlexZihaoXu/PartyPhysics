package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;

import java.io.IOException;
import java.util.LinkedList;

public class ClientManager implements Runnable {
    ClientSocket socket;
    private final Thread thread = new Thread(this, "Thread-ClientManager");
    private final LinkedList<Package> packagesQueue = new LinkedList<>();

    public ClientManager() {
        socket = new ClientSocket("localhost", PartyPhysicsGame.SERVER_PORT);
    }

    public void start() {
        socket.connect();
        thread.start();
    }

    public Package pull() {
        synchronized (packagesQueue) {
            if (packagesQueue.isEmpty()) {
                return null;
            }
            return packagesQueue.removeFirst();
        }
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                Package pkg = socket.pull();
                synchronized (packagesQueue) {
                    packagesQueue.addLast(pkg);
                }
                Thread.yield();
            }
        } catch (IOException ignored) {
        } finally {
            socket.close();
        }
    }

    public void close() {
        socket.close();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public void send(Package pkg) {
        socket.send(pkg);
    }
}
