package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

public class HostingClient implements ClientEventHandler {

    private final ClientSocket socket;
    private final Thread hostingClientThread;

    private final HostingServer server;

    private boolean socketShouldStop = false;

    private double latency = 0;

    private String clientName = null;

    private final int id;

    public int getID() {
        return id;
    }

    public String getName() {
        return clientName;
    }

    public HostingClient(HostingServer server, ClientSocket socket, int id) {
        this.id = id;
        this.socket = socket;
        this.server = server;
        hostingClientThread = new Thread(this::receivingLoop, "HostingClient-Thread (" + socket.getSocket().getRemoteSocketAddress() + ")");
        hostingClientThread.start();
        log("new connection.");
    }

    public void log(String info) {
        if (clientName == null)
            System.out.println("Client[" + this.socket.getSocket().getRemoteSocketAddress() + "]: " + info);
        else
            System.out.println("<" + clientName + ">[" + this.socket.getSocket().getRemoteSocketAddress() + "]: " + info);
    }

    public ClientSocket getSocket() {
        return socket;
    }

    public void shutdown() {
        socketShouldStop = true;
        if (!this.socket.getSocket().isClosed())
            this.socket.close();
    }

    public boolean isClosed() {
        return socket.isClosed() || socketShouldStop;
    }

    public void send(Package pkg) {
        if (!socket.isClosed()) {
            socket.send(pkg);
        }
    }

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

    private void handShake(Package pkg) {
        log("Set player name to " + pkg.getString("name") + ".");
        clientName = pkg.getString("name");
        log("Joined game with id: " + getID() + ".");

        server.joinClient(this);
    }

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

    public double getLatency() {
        return latency;
    }

    public void transfer() {
        socket.transfer();
    }

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
