package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.PartyPhysicsGame;
import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;

import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedList;

public class JoiningClient implements ServerClientType {
    private ClientSocket socket;
    private boolean socketShouldClose = false;

    private String crashLog = null;
    private boolean isConnected = false;

    private final LinkedList<Package> recvQueue = new LinkedList<>();
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
            System.out.println("is connected");
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

                synchronized (recvQueue) {
                    recvQueue.addLast(socket.pull());
                }
                Thread.yield();
            }
            System.out.println("normal shutdown");
        } catch (Exception e) {
            crashLog = e.getMessage();
            e.printStackTrace();
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
        synchronized (recvQueue) {
            if (recvQueue.isEmpty()) {
                return null;
            }
            return recvQueue.removeFirst();
        }
    }

    public String getCrashLog() {
        return crashLog;
    }

    public boolean isConnecting() {
        return connecting;
    }
}
