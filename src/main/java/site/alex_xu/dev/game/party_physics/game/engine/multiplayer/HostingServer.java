package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.content.player.LocalPlayerController;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameWorld;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.sync.ServerSideWorldSyncer;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ServerSocket;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.*;

public class HostingServer implements ServerClientType {

    private static boolean shutdownHookAdded = false;
    static HashSet<HostingServer> servers = new HashSet<>();
    private ServerSocket serverSocket;
    private boolean serverRunning = false;

    private String serverCrashLog = null;

    private final Thread serverThread = new Thread(this::mainLoop, "HostingServer-Thread");

    final LinkedList<Package> recvQueueOut = new LinkedList<>();
    final LinkedList<Package> recvQueueIn = new LinkedList<>();

    private final TreeMap<Integer, HostingClient> hostingClients = new TreeMap<>();

    private final TreeMap<Integer, Client> joinedClients = new TreeMap<>();

    private ServerSideWorldSyncer worldSyncer;

    public String getServerCrashLog() {
        return serverCrashLog;
    }

    public boolean isServerRunning() {
        return serverRunning && !serverSocket.isClosed();
    }

    @Override
    public boolean isCrashed() {
        return serverCrashLog != null;
    }

    private final String name;


    public ServerSideWorldSyncer getWorldSyncer() {
        return worldSyncer;
    }

    public GameWorld getSyncedWorld() {
        if (getWorldSyncer() == null)
            return null;
        return getWorldSyncer().getWorld();
    }


    public HostingServer(String name) {
        this.name = name;
        servers.add(this);
        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(HostingServer::cleanup, "HostingServerShutdownHook"));
            shutdownHookAdded = true;
        }
    }

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

    public void kickClient(Client client) {
        kickClient(client.getID());
    }

    public void kickClient(HostingClient client) {
        kickClient(client.getID());

    }

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
                    ClientSocket socket = serverSocket.accept();

                    int id = (int) (Math.random() * 100) * 100;
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

    public void shutdown() {
        serverRunning = false;
        if (serverSocket != null)
            serverSocket.close();
        for (HostingClient client : hostingClients.values()) {
            client.shutdown();

        }
        servers.remove(this);
    }

    @Override
    public void flush() {
        for (HostingClient client : hostingClients.values()) {
            client.transfer();
        }
    }

    @Override
    public Package pull() {
        synchronized (recvQueueOut) {
            if (recvQueueOut.isEmpty()) {
                return null;
            }
            return recvQueueOut.removeFirst();
        }
    }

    public static void cleanup() {
        for (HostingServer server : servers) {
            server.shutdown();
        }
    }

    public void tick() {
        while (!recvQueueIn.isEmpty()) {
            Package pkg = recvQueueIn.removeFirst();
            if (getWorldSyncer() != null)
                getWorldSyncer().handlePackage(pkg);
            recvQueueOut.addLast(pkg);
        }
        if (getWorldSyncer() != null) {
            getWorldSyncer().tick();
        }
        flush();
    }

    public String getName() {
        return name;
    }

    public Collection<HostingClient> getHostingClients() {
        return hostingClients.values();
    }

    public void broadcast(Package pkg) {
        for (HostingClient client : hostingClients.values()) {
            client.send(pkg);
        }
    }

    public Client getClient(int id) {
        if (joinedClients.containsKey(id)) {
            return joinedClients.get(id);
        }
        return null;
    }

    public Client getClient(HostingClient hostingClient) {
        return getClient(hostingClient.getID());
    }

    public HostingClient getHostingClient(int id) {
        if (hostingClients.containsKey(id)) {
            return hostingClients.get(id);
        }
        return null;
    }

    public HostingClient getHostingClient(Client client) {
        return getHostingClient(client.getID());
    }

    public LocalPlayerController getLocalPlayerController() {
        if (worldSyncer != null) {
            return worldSyncer.getLocalPlayerController();
        }
        return null;
    }
}
