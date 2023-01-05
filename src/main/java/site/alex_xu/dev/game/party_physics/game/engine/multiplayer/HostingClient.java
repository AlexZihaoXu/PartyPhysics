package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

public class HostingClient {


    private final ClientSocket socket;
    private final Thread hostingClientThread;

    private final HostingServer server;

    private boolean socketShouldStop = false;

    private String clientName = null;

    public String getName() {
        return clientName;
    }

    public HostingClient(HostingServer server, ClientSocket socket) {
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
                            e.getMessage().toLowerCase().contains("socket closed")||
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

    private void onPackageReceived(Package pkg) {

        if (pkg.getType() == PackageTypes.HANDSHAKE) {
            log("Set player name to " + pkg.getString("name"));
            clientName = pkg.getString("name");
        }

        synchronized (server.recvQueue) {
            server.recvQueue.addLast(pkg);
        }
    }

    public void flush() {
        socket.flush();
    }
}
