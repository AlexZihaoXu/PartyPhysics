package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.content.player.LocalPlayerController;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync.ClientSideWorldSyncer;
import site.alex_xu.dev.game.party_physics.game.engine.networking.WrapUpSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.io.EOFException;
import java.net.SocketException;
import java.util.*;

/**
 * A Client that joins a Server
 */
public class JoiningClient implements ServerClientType, ClientEventHandler {

    /**
     * A cache of all unclosed clients for auto clean up after program shutdown
     */
    private static final HashSet<JoiningClient> unclosedClients = new HashSet<>();

    /**
     * The wrap up socket to transfer messages with the server
     */
    private WrapUpSocket socket;
    private boolean socketShouldClose = false;

    /**
     * All clients that have connected to the server
     */
    private final TreeMap<Integer, Client> clients = new TreeMap<>();

    /**
     * Stores crash log (if crashed)
     */
    private String crashLog = null;
    private boolean isConnected = false;

    private String hostName = null;

    /**
     * The receiving queue that stores packages just received
     */
    private final LinkedList<Package> recvQueueIn = new LinkedList<>();
    /**
     * The receiving queue that stores processed packages after getting received
     */
    private final LinkedList<Package> recvQueueOut = new LinkedList<>();

    /**
     * A queue for sending packages
     */
    private final LinkedList<Package> sendQueue = new LinkedList<>();

    /**
     * A thread that receives new packages
     */
    private final Thread recvThread = new Thread(this::recvLoop, "JoiningClientRecvThread");

    private final String ip;

    /**
     * A clock that calculates the network latency
     */
    private final Clock pingClock = new Clock();

    /**
     * A clock that sends out a latency test package based on a period of time
     */
    private final Clock pingClockTimer = new Clock();

    /**
     * True only when the client is on a state of connecting
     */
    private boolean connecting = false;

    private double latency = 0;

    /**
     * Stores own client object associated with this networking joining client object
     */
    private Client ownClient = null;

    /**
     * Name of the client
     */
    private String name;

    /**
     * The world syncer associated with this joining client object
     */
    private ClientSideWorldSyncer worldSyncer;

    /**
     * @param ip the ip address to connect to
     */
    public JoiningClient(String ip) {
        this.ip = ip;
    }

    /**
     * @return true if is currently connected to a server
     */
    public boolean isConnected() {
        return isConnected && !isClosed();
    }

    /**
     * @return the very own client object of this "joining client"
     */
    public Client getOwnClient() {
        return ownClient;
    }


    /**
     * @return the world syncer of the joining client
     */
    public ClientSideWorldSyncer getWorldSyncer() {
        return worldSyncer;
    }

    /**
     * @return the game world object from the world syncer
     * returns null if the syncer or world has not been created yet
     * A shortcut to get world syncer instead of calling getWorldSyncer().getWorld()
     */
    public GameWorld getSyncedWorld() {
        if (getWorldSyncer() == null)
            return null;
        return getWorldSyncer().getWorld();
    }

    /**
     * Connect to the target server
     */
    public void connect() {
        connecting = true;
        try {
            socket = new WrapUpSocket(ip, PartyPhysicsGame.SERVER_PORT);
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

    /**
     * A loop that keeps pulling new packages from socket binaries streams
     */
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

    /**
     * @return all clients
     */
    public Collection<Client> getClients() {
        return clients.values();
    }

    /**
     * @param pkg the package to send
     * Packages will not be sent until flush() is called
     */
    public void send(Package pkg) {
        sendQueue.addLast(pkg);
    }

    /**
     * @return true if the joining client was crashed
     */
    @Override
    public boolean isCrashed() {
        return crashLog != null;
    }

    /**
     * Shutdown this joining client
     */
    @Override
    public void shutdown() {
        socketShouldClose = true;
        if (!socket.isClosed())
            socket.close();
        unclosedClients.remove(this);
    }

    /**
     * @return true if the client is closed
     */
    public boolean isClosed() {
        return socket != null && socket.isClosed();
    }

    /**
     * Sends out everything in the queue
     */
    @Override
    public void flush() {
        if (socket != null) {
            while (!sendQueue.isEmpty()) {
                socket.send(sendQueue.removeFirst());
            }
            socket.transfer();
        }
    }

    /**
     * @return a package from the receiving queue, null if the queue is empty
     */
    @Override
    public Package pull() {
        synchronized (recvQueueIn) {
            if (recvQueueOut.isEmpty()) {
                return null;
            }
            return recvQueueOut.removeFirst();
        }
    }

    /**
     * @return the crash log (if crashed)
     */
    public String getCrashLog() {
        return crashLog;
    }

    /**
     * @return true if current status is connecting
     */
    public boolean isConnecting() {
        return connecting;
    }

    /**
     * Process everything for 1 tick
     */
    public void tick() {

        if (pingClockTimer.elapsedTime() > 1) {
            Package pkg = new Package(PackageTypes.PING);
            pkg.setFraction("time", pingClock.elapsedTime());
            send(pkg);
            pingClockTimer.reset();
        }

        synchronized (recvQueueIn) {
            while (!recvQueueIn.isEmpty()) {
                Package pkg = recvQueueIn.removeFirst();
                processPackage(pkg);
                if (getWorldSyncer() != null) {
                    getWorldSyncer().handlePackage(pkg);
                }
                recvQueueOut.addLast(pkg);
            }
        }


        if (getLocalController() != null) {
            while (true) {
                Package pkg = getLocalController().pull();
                if (pkg == null) break;
                send(pkg);
            }
        }

        if (getWorldSyncer() != null) {
            getWorldSyncer().tick();
        }

        flush();
    }

    /**
     * @param id the ID of the client
     * @return the client with the given ID, null if not found
     */
    public Client getClient(int id) {
        if (clients.containsKey(id)) {
            return clients.get(id);
        }
        return null;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param pkg the package to process
     */
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
            int id = pkg.getInteger("id");
            if (ownClient != null && ownClient.getID() == id) {
                onClientJoin(ownClient);
            } else {
                Client client = new Client(id, pkg.getString("name"));
                clients.put(client.getID(), client);
                onClientJoin(client);
            }
        } else if (pkg.getType() == PackageTypes.CLIENT_LEAVE) {
            Client client = getClient(pkg.getInteger("id"));
            onClientLeave(client);
            clients.remove(client.getID());
        }
    }

    /**
     * Clean up and shutdown everything
     */
    public static void cleanup() {
        ArrayList<JoiningClient> clients = new ArrayList<>(unclosedClients);
        for (JoiningClient client : clients) {
            client.shutdown();
        }
    }

    /**
     * @return the host's name
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param client new joined client
     * Gets called when a new client joins
     */
    @Override
    public void onClientJoin(Client client) {
        System.out.println(client.getName() + " joined.");
        if (worldSyncer != null) {
            worldSyncer.onClientJoin(client);
        }
    }

    /**
     * @param client the leaving client
     * Gets called when a client leaves
     */
    @Override
    public void onClientLeave(Client client) {
        System.out.println(client.getName() + " left.");
        if (worldSyncer != null) {
            worldSyncer.onClientLeave(client);
        }
    }

    /**
     * @return the local controller
     */
    public LocalPlayerController getLocalController() {
        if (worldSyncer != null)
            return worldSyncer.getLocalController();
        return null;
    }
}
