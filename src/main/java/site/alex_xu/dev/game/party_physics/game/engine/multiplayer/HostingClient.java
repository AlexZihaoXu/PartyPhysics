package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.game.engine.networking.WrapUpSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

/**
 * Hosting client
 * (NOT THE ABSTRACTED CLIENT THAT DOES NOT HAVE CONNECTION)
 * establishes a connection to the joining client on the remote side
 */
public class HostingClient implements ClientEventHandler {

    /**
     * The wrap up socket for communication
     */
    private final WrapUpSocket socket;
    /**
     * Main thread for hosting client
     */
    private final Thread hostingClientThread;

    /**
     * The server of the hosting client
     */
    private final HostingServer server;

    private boolean socketShouldStop = false;

    private double latency = 0;

    private String clientName = null;

    private final int id;

    /**
     * @return the ID of the client
     */
    public int getID() {
        return id;
    }

    /**
     * @return the name of the client
     */
    public String getName() {
        return clientName;
    }

    /**
     * @param server the server who created the connection
     * @param socket the socket with the connection
     * @param id the assigned ID
     */
    public HostingClient(HostingServer server, WrapUpSocket socket, int id) {
        this.id = id;
        this.socket = socket;
        this.server = server;
        hostingClientThread = new Thread(this::receivingLoop, "HostingClient-Thread (" + socket.getSocket().getRemoteSocketAddress() + ")");
        hostingClientThread.start();
        log("new connection.");
    }

    /**
     * @param info the information to log
     */
    public void log(String info) {
        if (clientName == null)
            System.out.println("Client[" + this.socket.getSocket().getRemoteSocketAddress() + "]: " + info);
        else
            System.out.println("<" + clientName + ">[" + this.socket.getSocket().getRemoteSocketAddress() + "]: " + info);
    }

    /**
     * @return the wrap up socket
     */
    public WrapUpSocket getSocket() {
        return socket;
    }

    /**
     * Shutdown and close everything
     */
    public void shutdown() {
        socketShouldStop = true;
        if (!this.socket.getSocket().isClosed())
            this.socket.close();
    }

    /**
     * @return true if closed
     */
    public boolean isClosed() {
        return socket.isClosed() || socketShouldStop;
    }

    /**
     * @param pkg package to send
     * (will not be sent until transfer() is called)
     */
    public void send(Package pkg) {
        if (!socket.isClosed()) {
            socket.send(pkg);
        }
    }

    /**
     * The loop for pulling packages from socket's binaries stream
     */
    private void receivingLoop() {
        try {
            while (!socketShouldStop && server.isServerRunning()) {
                Package pkg = socket.pull();
                onPackageReceived(pkg);
                Thread.yield();
            }
        } catch (IOException e) {
            if (!(
                    e instanceof SocketException && (
                            e.getMessage().toLowerCase().contains("socket closed") ||
                                    e.getMessage().toLowerCase().contains("connection reset")
                    ) ||
                            e instanceof EOFException
            ))
                throw new RuntimeException(e);
        } finally {
            socketShouldStop = true;
            if (!socket.isClosed()) {
                socket.close();
            }
            log("connection lost.");
        }
    }

    /**
     * @param pkg handle handshake package
     */
    private void handShake(Package pkg) {
        log("Set player name to " + pkg.getString("name") + ".");
        clientName = pkg.getString("name");
        log("Joined game with id: " + getID() + ".");

        server.joinClient(this);
    }

    /**
     * @param pkg the package to process
     */
    private void onPackageReceived(Package pkg) {

        if (pkg.getType() == PackageTypes.HANDSHAKE) {
            handShake(pkg);
        } else if (pkg.getType() == PackageTypes.PING) {
            Package reply = new Package(PackageTypes.PONG);
            reply.setFraction("time", pkg.getFraction("time"));
            send(reply);
        } else if (pkg.getType() == PackageTypes.CLIENT_UPDATE_LATENCY) {
            latency = pkg.getFraction("latency");
            server.getClient(this).latency = latency;
            Package lpkg = new Package(PackageTypes.CLIENT_UPDATE_LATENCY);
            lpkg.setInteger("id", getID());
            lpkg.setFraction("latency", latency);
            for (HostingClient clt : server.getHostingClients()) {
                clt.send(lpkg);
            }
        }

        synchronized (server.recvQueueIn) {
            server.recvQueueIn.addLast(pkg);
        }
    }

    /**
     * @return the latency
     */
    public double getLatency() {
        return latency;
    }

    /**
     * Sends out everything that are buffered
     */
    public void transfer() {
        socket.transfer();
    }

    /**
     * @param client the joined client
     */
    @Override
    public void onClientJoin(Client client) {
        if (client.getID() == getID()) {
            Package joinPkg = client.createJoinPackage();
            HostingClient hostingClient = server.getHostingClient(client);
            for (HostingClient clt : server.getHostingClients()) {
                if (clt.getID() != getID()) {
                    clt.send(joinPkg);
                    Package pkg = server.getClient(clt).createJoinPackage();
                    hostingClient.send(pkg);
                }
            }
        }
    }

    /**
     * @param client the client who left
     */
    @Override
    public void onClientLeave(Client client) {
        if (client.getID() == getID()) {
            Package leavePackage = client.createLeavePackage();
            for (HostingClient clt : server.getHostingClients()) {
                if (clt.getID() != getID()) {
                    clt.send(leavePackage);
                }
            }
        }
    }
}
