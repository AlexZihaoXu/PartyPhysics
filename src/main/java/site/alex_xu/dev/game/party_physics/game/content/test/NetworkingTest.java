package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.game.engine.networking.ClientSocket;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

import java.util.Arrays;

public class NetworkingTest {
    public static void main(String[] args) {
        ClientSocket socket = new ClientSocket("localhost", 3000);
        socket.connect();

        Package pkg = new Package(PackageTypes.HANDSHAKE);
        pkg.setFraction("player.x", 10);
        pkg.setFraction("player.y", 10);
        pkg.setString("player.name", "Alex");
        System.out.println(pkg);

        socket.send(pkg);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            pkg = socket.pull();
            System.out.println(pkg);
        }

    }
}
