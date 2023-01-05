package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class JoiningClient implements ServerClientType {

    private static final HashSet<JoiningClient> unclosedClients = new HashSet<>();
    private ClientSocket socket;
    private boolean socketShouldClose = false;

    private String crashLog = null;
    private boolean isConnected = false;

    private String hostName = null;

    private final LinkedList<Package> recvQueueIn = new LinkedList<>();
    private final LinkedList<Package> recvQueueOut = new LinkedList<>();
    private final LinkedList<Package> sendQueue = new LinkedList<>();

    private final Thread recvThread = new Thread(this::recvLoop, "JoiningClientRecvThread");

    private final String ip;

    private boolean connecting = false;

    public JoiningClient(String ip) {
        this.ip = ip;
    }


    public boolean isConnected() {
        return isConnected && !isClosed();
    }

    public void connect() {
        connecting = true;
        try {
            socket = new ClientSocket(ip, PartyPhysicsGame.SERVER_PORT);
            socket.connect();
            isConnected = true;
            unclosedClients.add(this);
            recvThread.start();

        } catch (Exception e) {
            crashLog = e.getMessage();
            e.printStackTrace();
        } finally {
            connecting = false;
        }
    }

    private void recvLoop() {
        try {
            while (!socketShouldClose && !socket.isClosed()) {
                Package pkg = socket.pull();
                synchronized (recvQueueIn) {
                    recvQueueIn.addLast(pkg);
                }
                Thread.yield();
            }
        } catch (Exception e) {
            if (!(
                    e instanceof SocketException && e.getMessage().toLowerCase().contains("socket closed")
            )) {
                crashLog = e.getMessage();
                e.printStackTrace();
            }
        } finally {
            shutdown();
        }
    }

    public void send(Package pkg) {
        sendQueue.addLast(pkg);
    }

    @Override
    public boolean isCrashed() {
        return crashLog != null;
    }

    @Override
    public void shutdown() {
        socketShouldClose = true;
        if (!socket.isClosed())
            socket.close();
        unclosedClients.remove(this);
    }

    public boolean isClosed() {
        return socket != null && socket.isClosed();
    }

    @Override
    public void flush() {
        if (socket != null) {
            while (!sendQueue.isEmpty()) {
                socket.send(sendQueue.removeFirst());
            }
            socket.flush();
        }
    }

    @Override
    public Package pull() {
        synchronized (recvQueueIn) {
            if (recvQueueIn.isEmpty()) {
                return null;
            }
            return recvQueueIn.removeFirst();
        }
    }

    public String getCrashLog() {
        return crashLog;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public void tick() {

        while (!recvQueueIn.isEmpty()) {
            Package pkg = recvQueueIn.removeFirst();
            processPackage(pkg);
            recvQueueOut.addLast(pkg);
        }

        flush();
    }

    public void processPackage(Package pkg) {
        System.out.println(pkg);
        if (pkg.getType() == PackageTypes.HANDSHAKE) {
            hostName = pkg.getString("name");
        }
    }

    public static void cleanup() {
        ArrayList<JoiningClient> clients = new ArrayList<>(unclosedClients);
        for (JoiningClient client : clients) {
            client.shutdown();
        }
    }

    public String getHostName() {
        return hostName;
    }
}
