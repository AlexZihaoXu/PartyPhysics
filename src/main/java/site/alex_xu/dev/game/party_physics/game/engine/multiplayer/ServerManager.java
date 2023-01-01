package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ServerSocket;

import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeMap;

public class ServerManager implements Runnable {
    ServerSocket socket = new ServerSocket(PartyPhysicsGame.SERVER_PORT);
    TreeMap<Integer, Client> clientsMap = new TreeMap<>();

    final LinkedList<Package> packagesQueue = new LinkedList<>();

    private final Thread thread = new Thread(this, "Thread-ServerManager");


    public void start() {
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
        System.out.println("Server listening on port: " + PartyPhysicsGame.SERVER_PORT);
        int clientIDCounter = 0;
        try {
            while (!socket.isClosed()) {
                ClientSocket s = socket.accept();
                System.out.println("New connection from: " + s.getSocket().getRemoteSocketAddress().toString());
                int id = clientIDCounter++;
                Client client = new Client(id, this, s);
                client.thread.start();
                clientsMap.put(id, client);

                Thread.yield();
            }
        } catch (IOException ignored) {
            socket.close();
        }
        System.out.println("Server closed.");
    }

    public void close() {
        socket.close();
    }

    public void broadCast(Package pkg) {
        for (Client client : clientsMap.values()) {
            client.send(pkg);
        }
    }

    public Client getClient(Package pkg) {
        return clientsMap.get(pkg.getInteger("clientID"));
    }

    public void flush() {
        for (Client client : clientsMap.values()) {
            client.flush();
        }
    }
}
