package site.alex_xu.dev.game.party_physics;

import site.alex_xu.dev.game.party_physics.game.content.test.*;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Networking;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;

public class PartyPhysicsGame {
    public static int SERVER_PORT = 2048;
    public static void main(String[] args) {
        Networking.getInstance().init();
        PartyPhysicsWindow.getInstance().changeStage(new ItemTestingStage());
        PartyPhysicsWindow.getInstance().start();
    }
}
