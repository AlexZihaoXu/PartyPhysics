package site.alex_xu.dev.game.party_physics.game.engine.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientSocket {
    private final String host;
    private final int port;
    private Socket socket = null;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public ClientSocket(String host, int port) {
        this.host = host;
        this.port = port;
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

    public Package pull() {
        return new Package(inputStream);
    }
}
