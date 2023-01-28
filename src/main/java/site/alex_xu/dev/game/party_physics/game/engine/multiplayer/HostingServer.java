package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.content.player.LocalPlayerController;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync.ServerSideWorldSyncer;
import site.alex_xu.dev.game.party_physics.game.engine.networking.WrapUpSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ServerSocket;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * A server that hosts client
 * should be instantiated on the server side
 */
public class HostingServer implements ServerClientType {

    private static boolean shutdownHookAdded = false;
    /**
     * A cache of all servers to clean up before the program shuts down
     */
    static HashSet<HostingServer> servers = new HashSet<>();
    /**
     * A socket that accepts new connections
     */
    private ServerSocket serverSocket;
    private boolean serverRunning = false;

    /**
     * Crash log (if crashed)
     */
    private String serverCrashLog = null;

    /**
     * A thread that executes the main loop of the server
     */
    private final Thread serverThread = new Thread(this::mainLoop, "HostingServer-Thread");

    /**
     * The receiving queue that stores packages just received
     */
    final LinkedList<Package> recvQueueIn = new LinkedList<>();
    /**
     * The receiving queue that stores processed packages after getting received
     */
    final LinkedList<Package> recvQueueOut = new LinkedList<>();

    /**
     * All hosting clients that has connections to the joining client
     */
    private final TreeMap<Integer, HostingClient> hostingClients = new TreeMap<>();

    /**
     * All clients
     */
    private final TreeMap<Integer, Client> joinedClients = new TreeMap<>();

    /**
     * The world syncer
     */
    private ServerSideWorldSyncer worldSyncer;

    /**
     * @return the crash log (if crashed)
     */
    public String getServerCrashLog() {
        return serverCrashLog;
    }

    /**
     * @return true if the server is running
     */
    public boolean isServerRunning() {
        return serverRunning && !serverSocket.isClosed();
    }

    /**
     * @return true if server is crashed
     */
    @Override
    public boolean isCrashed() {
        return serverCrashLog != null;
    }

    private final String name;


    /**
     * @return the world syncer
     */
    public ServerSideWorldSyncer getWorldSyncer() {
        return worldSyncer;
    }

    /**
     * @return world from world syncer if they exist, otherwise null
     */
    public GameWorld getSyncedWorld() {
        if (getWorldSyncer() == null)
            return null;
        return getWorldSyncer().getWorld();
    }


    /**
     * @param name the name of the host
     */
    public HostingServer(String name) {
        this.name = name;
        servers.add(this);
        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(HostingServer::cleanup, "HostingServerShutdownHook"));
            shutdownHookAdded = true;
        }
    }

    /**
     * @param id the id of the client to kick
     * Should be called when error occurs such as connection lost, or client request to leave
     */
    public void kickClient(int id) {
        if (hostingClients.containsKey(id)) {
            HostingClient client = hostingClients.get(id);
            if (joinedClients.containsKey(id)) {
                client.onClientLeave(getClient(client));
                worldSyncer.onClientLeave(getClient(client));
            }
            joinedClients.remove(id);
            hostingClients.remove(id);
            client.shutdown();
        }
    }

    /**
     * @param client the client to kick
     * A shortcut for kickClient(client.getID());
     */
    public void kickClient(Client client) {
        kickClient(client.getID());
    }

    /**
     * @param client the hosting client to kick
     * A shortcut for kickClient(client.getID());
     */
    public void kickClient(HostingClient client) {
        kickClient(client.getID());
    }

    /**
     * The main loop of the server thread
     */
    private void mainLoop() {
        try {
            serverSocket = new ServerSocket(PartyPhysicsGame.SERVER_PORT);
            ServerSocket serverSocket = this.serverSocket;
            System.out.println("Server listening on port: " + PartyPhysicsGame.SERVER_PORT);
            while (serverRunning) {
                serverSocket.getSocket().setSoTimeout(250);
                ArrayList<HostingClient> clients = new ArrayList<>(this.hostingClients.values());
                for (HostingClient client : clients) {
                    if (client.isClosed()) {
                        kickClient(client);
                    }
                }
                try {
                    WrapUpSocket socket = serverSocket.accept();

                    int id = (int) (1 + Math.random() * 100) * 100;
                    while (hostingClients.containsKey(id)) {
                        id++;
                    }
                    socket.getSocket().setSoTimeout(3 * 60 * 1000); // 1 min timeout
                    this.hostingClients.put(id, new HostingClient(this, socket, id));
                } catch (SocketTimeoutException ignored) {
                }
                Thread.yield();
            }
        } catch (Exception exception) {
            if (!(exception instanceof SocketException && exception.getMessage().contains("configureBlocking"))) {
                serverCrashLog = exception.getMessage();
                throw new RuntimeException(exception);
            }
        } finally {
            serverRunning = false;
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (serverCrashLog != null) {
                System.err.println(serverCrashLog);

            }
            shutdown();
            System.out.println("Server stopped!");
        }
    }

    /**
     * @param client make a client join the world
     * This will send out all the necessary packages for the client to create world and join
     */
    void joinClient(HostingClient client) {

        Client clt = new Client(client.getID(), client.getName());
        joinedClients.put(clt.getID(), clt);

        Package pkg = new Package(PackageTypes.HANDSHAKE);
        pkg.setString("name", name);
        pkg.setInteger("id", client.getID());
        client.send(pkg);

        worldSyncer.onClientJoin(clt); // add player model to the client
        client.onClientJoin(clt);
        for (Client c : joinedClients.values()) {
            client.send(c.createJoinPackage());
        }

    }

    /**
     * Start the server
     */
    public void launch() {
        if (serverRunning)
            throw new IllegalStateException("Server is already running!");
        serverRunning = true;
        serverThread.start();

        Client client = new Client(0, name);
        joinedClients.put(client.getID(), client);
        worldSyncer = new ServerSideWorldSyncer(this);
        worldSyncer.onClientJoin(client);

    }

    /**
     * Shutdown the server
     */
    public void shutdown() {
        serverRunning = false;
        if (serverSocket != null)
            serverSocket.close();
        for (HostingClient client : hostingClients.values()) {
            client.shutdown();
        }
        servers.remove(this);
    }

    /**
     * Send out all packages
     */
    @Override
    public void flush() {
        ArrayList<HostingClient> removed = new ArrayList<>();
        for (HostingClient client : hostingClients.values()) {
            try {
                client.transfer();
            } catch (RuntimeException ignored) {
                removed.add(client);
            }
        }
        for (HostingClient client : removed) {
            kickClient(client);
        }
    }

    /**
     * @return a network package if the queue is not empty, otherwise null
     */
    @Override
    public Package pull() {
        synchronized (recvQueueOut) {
            if (recvQueueOut.isEmpty()) {
                return null;
            }
            return recvQueueOut.removeFirst();
        }
    }

    /**
     * Clean up and shutdown everything
     */
    public static void cleanup() {
        for (HostingServer server : servers) {
            server.shutdown();
        }
    }

    /**
     * Tick once
     * processes the packages and tick the world
     */
    public void tick() {
        try {
            synchronized (recvQueueIn) {
                while (!recvQueueIn.isEmpty()) {
                    Package pkg = recvQueueIn.removeFirst();
                    if (getWorldSyncer() != null)
                        getWorldSyncer().handlePackage(pkg);
                    recvQueueOut.addLast(pkg);
                }
            }
            if (getWorldSyncer() != null) {
                getWorldSyncer().tick();
            }

            flush();
        } catch (Exception e) {
            serverCrashLog = e.getMessage();
            e.printStackTrace();
            shutdown();
        }

    }

    /**
     * @return the host's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return a list of all hosting clients
     */
    public Collection<HostingClient> getHostingClients() {
        return hostingClients.values();
    }

    /**
     * @param pkg a package to be sent to all clients
     */
    public void broadcast(Package pkg) {
        for (HostingClient client : hostingClients.values()) {
            client.send(pkg);
        }
    }

    /**
     * @param id id of the client
     * @return the client with the given id
     */
    public Client getClient(int id) {
        if (joinedClients.containsKey(id)) {
            return joinedClients.get(id);
        }
        return null;
    }

    /**
     * @param hostingClient hosting client of the client
     * @return the client of the hosting client
     */
    public Client getClient(HostingClient hostingClient) {
        return getClient(hostingClient.getID());
    }

    /**
     * @param id the id to get hosting client
     * @return the hosting client from the given ID
     */
    public HostingClient getHostingClient(int id) {
        if (hostingClients.containsKey(id)) {
            return hostingClients.get(id);
        }
        return null;
    }

    /**
     * @param client the client to find hosting client
     * @return the hosting client from the client
     */
    public HostingClient getHostingClient(Client client) {
        return getHostingClient(client.getID());
    }

    /**
     * @return the local player controller
     */
    public LocalPlayerController getLocalPlayerController() {
        if (worldSyncer != null) {
            return worldSyncer.getLocalPlayerController();
        }
        return null;
    }
}
