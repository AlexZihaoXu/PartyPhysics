package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;

public class HostingClient {


    private final ClientSocket socket;
    private final Thread hostingClientThread;

    private final HostingServer server;

    private boolean socketShouldStop = false;


    public HostingClient(HostingServer server, ClientSocket socket) {
        this.socket = socket;
        this.server = server;
        hostingClientThread = new Thread(this::receivingLoop, "HostingClient-Thread (" + socket.getSocket().getRemoteSocketAddress() + ")");
        hostingClientThread.start();
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
                synchronized (server.recvQueue) {
                    server.recvQueue.addLast(pkg);
                }
                Thread.yield();
            }
        } catch (IOException e) {
            if (!(
                    e instanceof SocketException && e.getMessage().toLowerCase().contains("socket closed") ||
                            e instanceof EOFException
            ))
                throw new RuntimeException(e);
        } finally {
            socketShouldStop = true;
            if (!socket.isClosed()) {
                socket.close();
            }
        }
    }

    public void flush() {
        socket.flush();
    }
}
