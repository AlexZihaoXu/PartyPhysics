package site.alex_xu.dev.game.party_physics.game.engine.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSocket {
    private String host;
    private int port;
    private Socket socket = null;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public ClientSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
    }

    public void connect() {
        try {
            Socket socket = new Socket(host, port);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            this.socket = socket;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Package pkg) {
        pkg.writeStream(outputStream);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Package pull() throws IOException {
        return new Package(inputStream);
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    public Socket getSocket() {
        return socket;
    }
}
