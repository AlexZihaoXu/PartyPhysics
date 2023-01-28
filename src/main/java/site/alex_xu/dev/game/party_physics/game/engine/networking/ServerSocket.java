package site.alex_xu.dev.game.party_physics.game.engine.networking;

import java.io.IOException;
import java.net.Socket;

/**
 * A server socket that handles connection from clients
 */
public class ServerSocket {

    java.net.ServerSocket socket;

    /**
     * @param port the port to bind
     * @throws IOException when IO error occurs
     */
    public ServerSocket(int port) throws IOException {
        this.socket = new java.net.ServerSocket(port);
    }

    /**
     * @return the original java socket
     */
    public java.net.ServerSocket getSocket() {
        return socket;
    }

    /**
     * @return true if the socket is closed
     */
    public boolean isClosed() {
        return socket.isClosed();
    }

    /**
     * Waits and accepts new connection.
     * @return a WrapUpSocket object that has a connection established to a client
     * @throws IOException when error occurs
     */
    public WrapUpSocket accept() throws IOException {
        Socket s = socket.accept();
        return new WrapUpSocket(s);
    }

    /**
     * Close the server socket
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
