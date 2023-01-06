package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync.ClientSideWorldSyncer;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.io.EOFException;
import java.net.SocketException;
import java.util.*;

public class JoiningClient implements ServerClientType, ClientEventHandler {

    private static final HashSet<JoiningClient> unclosedClients = new HashSet<>();
    private ClientSocket socket;
    private boolean socketShouldClose = false;

    private final TreeMap<Integer, Client> clients = new TreeMap<>();

    private String crashLog = null;
    private boolean isConnected = false;

    private String hostName = null;

    private final LinkedList<Package> recvQueueIn = new LinkedList<>();
    private final LinkedList<Package> recvQueueOut = new LinkedList<>();
    private final LinkedList<Package> sendQueue = new LinkedList<>();

    private final Thread recvThread = new Thread(this::recvLoop, "JoiningClientRecvThread");

    private final String ip;

    private final Clock pingClock = new Clock();

    private final Clock pingClockTimer = new Clock();

    private boolean connecting = false;

    private double latency = 0;

    private Client ownClient = null;

    private String name;

    private ClientSideWorldSyncer worldSyncer;

    public JoiningClient(String ip) {
        this.ip = ip;
    }

    public boolean isConnected() {
        return isConnected && !isClosed();
    }

    public Client getOwnClient() {
        return ownClient;
    }


    public ClientSideWorldSyncer getWorldSyncer() {
        return worldSyncer;
    }

    public GameWorld getSyncedWorld() {
        if (getWorldSyncer() == null)
            return null;
        return getWorldSyncer().getWorld();
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
                if (e instanceof EOFException) {
                    crashLog = "Server closed.";
                } else {
                    crashLog = e.getMessage();
                    e.printStackTrace();
                }
            }
        } finally {
            shutdown();
        }
    }

    public Collection<Client> getClients() {
        return clients.values();
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
            socket.transfer();
        }
    }

    @Override
    public Package pull() {
        synchronized (recvQueueIn) {
            if (recvQueueOut.isEmpty()) {
                return null;
            }
            return recvQueueOut.removeFirst();
        }
    }

    public String getCrashLog() {
        return crashLog;
    }

    public boolean isConnecting() {
        return connecting;
    }

    public void tick() {

        if (pingClockTimer.elapsedTime() > 1) {
            Package pkg = new Package(PackageTypes.PING);
            pkg.setFraction("time", pingClock.elapsedTime());
            send(pkg);
            pingClockTimer.reset();
        }

        synchronized (recvQueueIn){
            while (!recvQueueIn.isEmpty()) {
                Package pkg = recvQueueIn.removeFirst();
                processPackage(pkg);
                if (getWorldSyncer() != null) {
                    getWorldSyncer().handlePackage(pkg);
                }
                recvQueueOut.addLast(pkg);
            }
        }

        if (getWorldSyncer() != null) {
            getWorldSyncer().tick();
        }

        flush();
    }

    public Client getClient(int id) {
        if (clients.containsKey(id)) {
            return clients.get(id);
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void processPackage(Package pkg) {
        if (pkg.getType() == PackageTypes.HANDSHAKE) {
            worldSyncer = new ClientSideWorldSyncer(this);
            Client client = new Client(pkg.getInteger("id"), name);
            hostName = pkg.getString("name");
            clients.put(0, new Client(0, hostName));
            clients.put(client.getID(), client);
            ownClient = client;
        } else if (pkg.getType() == PackageTypes.PONG) {
            double lastTime = pkg.getFraction("time");
            latency = (pingClock.elapsedTime() - lastTime) * 1000;
            Package lpkg = new Package(PackageTypes.CLIENT_UPDATE_LATENCY);
            lpkg.setFraction("latency", latency);
            if (getOwnClient() != null) {
                getOwnClient().latency = latency;
            }
            send(lpkg);
        } else if (pkg.getType() == PackageTypes.CLIENT_UPDATE_LATENCY) {
            getClient(pkg.getInteger("id")).latency = pkg.getFraction("latency");
        } else if (pkg.getType() == PackageTypes.CLIENT_JOIN) {
            Client client = new Client(pkg.getInteger("id"), pkg.getString("name"));
            clients.put(client.getID(), client);
            onClientJoin(client);
        } else if (pkg.getType() == PackageTypes.CLIENT_LEAVE) {
            Client client = getClient(pkg.getInteger("id"));
            onClientLeave(client);
            clients.remove(client.getID());
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

    @Override
    public void onClientJoin(Client client) {
        System.out.println(client.getName() + " joined.");
        if (worldSyncer != null) {
            worldSyncer.onClientJoin(client);
        }
    }

    @Override
    public void onClientLeave(Client client) {
        System.out.println(client.getName() + " left.");
        if (worldSyncer != null) {
            worldSyncer.onClientLeave(client);
        }
    }
}
