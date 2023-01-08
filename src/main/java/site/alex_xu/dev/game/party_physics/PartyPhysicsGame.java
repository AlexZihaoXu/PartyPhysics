package site.alex_xu.dev.game.party_physics;

import site.alex_xu.dev.game.party_physics.game.content.stages.LoadingStage;
import site.alex_xu.dev.game.party_physics.game.content.stages.menu.MenuStage;
import site.alex_xu.dev.game.party_physics.game.engine.networking.Networking;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;

public class PartyPhysicsGame {
    public static final String VERSION_STRING = "dev";
    public static int SERVER_PORT = 2048;
    public static void main(String[] args) {
        Networking.getInstance().init();
        PartyPhysicsWindow.getInstance().changeStage(new LoadingStage());
        PartyPhysicsWindow.getInstance().start();
    }
}
