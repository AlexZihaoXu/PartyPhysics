package site.alex_xu.dev.game.party_physics;

import site.alex_xu.dev.game.party_physics.game.content.test.*;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;

public class PartyPhysicsGame {
    public static void main(String[] args) {
        PartyPhysicsWindow.getInstance().changeStage(new GameWorldTestStage());
        PartyPhysicsWindow.getInstance().start();
    }
}
