package site.alex_xu.dev.game.party_physics.game.engine.networking;

import java.io.IOException;
import java.net.Socket;

public class ServerSocket {

    java.net.ServerSocket socket;

    public ServerSocket(int port) {
        try {
            this.socket = new java.net.ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean isClosed() {
        return socket.isClosed();
    }

    public ClientSocket accept() throws IOException {
        Socket s = socket.accept();
        return new ClientSocket(s);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
