package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ServerSocket;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class HostingServer implements ServerClientType {

    private static boolean shutdownHookAdded = false;
    static HashSet<HostingServer> servers = new HashSet<>();
    private ServerSocket serverSocket;
    private boolean serverRunning = false;

    private String serverCrashLog = null;
    private Thread serverThread = new Thread(this::mainLoop, "HostingServer-Thread");

    final LinkedList<Package> recvQueue = new LinkedList<>();

    private final HashSet<HostingClient> clients = new HashSet<>();

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

    public HostingServer() {
        servers.add(this);
        if (!shutdownHookAdded) {
            Runtime.getRuntime().addShutdownHook(new Thread(HostingServer::cleanup, "HostingServerShutdownHook"));
            shutdownHookAdded = true;
        }
    }

    private void mainLoop() {
        try {
            serverSocket = new ServerSocket(PartyPhysicsGame.SERVER_PORT);
            ServerSocket serverSocket = this.serverSocket;
            System.out.println("Server listening at port: " + PartyPhysicsGame.SERVER_PORT);
            while (serverRunning) {
                serverSocket.getSocket().setSoTimeout(250);
                ArrayList<HostingClient> clients = new ArrayList<>(this.clients);
                for (HostingClient client : clients) {
                    if (client.isClosed()) {
                        this.clients.remove(client);
                    }
                }
                try {
                    ClientSocket socket = serverSocket.accept();
                    socket.getSocket().setSoTimeout(3 * 60 * 1000); // 1 min timeout
                    this.clients.add(new HostingClient(this, socket));
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

    public void launch() {
        if (serverRunning)
            throw new IllegalStateException("Server is already running!");
        serverRunning = true;
        serverThread.start();
    }

    public void shutdown() {
        serverRunning = false;
        if (serverSocket != null)
            serverSocket.close();
        for (HostingClient client : clients) {
            client.shutdown();
        }
        servers.remove(this);
    }

    @Override
    public void flush() {
        for (HostingClient client : clients) {
            client.flush();
        }
    }

    @Override
    public Package pull() {
        synchronized (recvQueue) {
            if (recvQueue.isEmpty()) {
                return null;
            }
            return recvQueue.removeFirst();
        }
    }

    public static void cleanup() {
        for (HostingServer server : servers) {
            server.shutdown();
        }
    }

    public void tick() {

    }

    public HashSet<HostingClient> getClients() {
        return clients;
    }

    public void broadcast(Package pkg) {
        for (HostingClient client : clients) {
            client.send(pkg);
        }
    }
}
