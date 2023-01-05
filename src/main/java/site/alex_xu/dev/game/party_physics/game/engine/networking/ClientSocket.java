package site.alex_xu.dev.game.party_physics.game.engine.networking;

import java.io.*;
import java.net.Socket;

public class ClientSocket {
    private String host;
    private int port;
    private Socket socket = null;
    private DataOutputStream outputStream;
    private BufferedOutputStream bufferedOutputStream;
    private DataInputStream inputStream;
    private BufferedInputStream bufferedInputStream;

    public ClientSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;
        bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        outputStream = new DataOutputStream(bufferedOutputStream);
        bufferedInputStream = new BufferedInputStream(socket.getInputStream());
        inputStream = new DataInputStream(bufferedInputStream);
    }

    public void connect() {
        try {
            Socket socket = new Socket(host, port);
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            outputStream = new DataOutputStream(bufferedOutputStream);
            bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            inputStream = new DataInputStream(bufferedInputStream);
            this.socket = socket;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(Package pkg) {
        pkg.writeStream(outputStream);
    }

    public void flush() {
        try {
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (socket != null)
                socket.close();
            bufferedInputStream.close();
            inputStream.close();
            bufferedOutputStream.close();
            outputStream.close();
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
