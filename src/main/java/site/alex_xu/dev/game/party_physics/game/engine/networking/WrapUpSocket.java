package site.alex_xu.dev.game.party_physics.game.engine.networking;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A wrap of java's original Socket, provides methods that sends out and retrieves packages
 */
public class WrapUpSocket {
    private String host;
    private int port;
    private Socket socket = null;

    private DataOutputStream outputStream;
    private BufferedOutputStream bufferedOutputStream;
    private DataInputStream inputStream;
    private BufferedInputStream bufferedInputStream;

    private final ReentrantLock writeLock = new ReentrantLock();
    private final ReentrantLock readLock = new ReentrantLock();

    private final LinkedList<Pair> sendQueue = new LinkedList<>();

    private static class Pair {
        public Package pkg;
        public long time;

        public Pair(Package pkg, long time) {
            this.time = time;
            this.pkg = pkg;
        }
    }


    /**
     * Create a socket that connects to the given address
     * @param host the host to connect
     * @param port the port to connect
     */
    public WrapUpSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @param socket the original socket
     * @throws IOException when any connection issue occurs
     */
    public WrapUpSocket(Socket socket) throws IOException {
        this.socket = socket;
        bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        outputStream = new DataOutputStream(bufferedOutputStream);
        bufferedInputStream = new BufferedInputStream(socket.getInputStream());
        inputStream = new DataInputStream(bufferedInputStream);
    }

    /**
     * Connect to the target host
     */
    public void connect() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 4000);
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            outputStream = new DataOutputStream(bufferedOutputStream);
            bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            inputStream = new DataInputStream(bufferedInputStream);
            this.socket = socket;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param pkg serialize and send out the package
     */
    public void send(Package pkg) {
        synchronized (sendQueue) {
            sendQueue.addLast(new Pair(pkg, System.currentTimeMillis()));
        }
    }

    /**
     * Retrieve all packages and sends out everything that was buffered
     */
    public void transfer() {
        if (writeLock.isLocked()) {
            throw new ConcurrentModificationException("ClientSocket.transfer was called twice at the same time");
        }
        writeLock.lock();

        synchronized (sendQueue) {
            long now = System.currentTimeMillis();
            while (!sendQueue.isEmpty()) { // TODO: completely remove the artificial latency before publish
                Package pkg = sendQueue.removeFirst().pkg;
                pkg.writeStream(outputStream);
            }
        }

        writeLock.unlock();
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

    /**
     * @return a deserialized package
     * @throws IOException when any connection issue occurs
     */
    public Package pull() throws IOException {
        if (readLock.isLocked()) {
            throw new ConcurrentModificationException("ClientSocket.pull was called twice at the same time");
        }
        readLock.lock();
        Package pkg = new Package(inputStream);
        readLock.unlock();
        return pkg;
    }

    /**
     * @return true if the socket is closed
     */
    public boolean isClosed() {
        return socket.isClosed();
    }

    /**
     * @return java's original socket
     */
    public Socket getSocket() {
        return socket;
    }
}