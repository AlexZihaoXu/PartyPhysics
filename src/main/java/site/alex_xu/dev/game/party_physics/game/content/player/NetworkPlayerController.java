package site.alex_xu.dev.game.party_physics.game.content.player;

import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;
import site.alex_xu.dev.game.party_physics.game.engine.networking.PackageTypes;

public class NetworkPlayerController extends PlayerController {
    public NetworkPlayerController(Player player) {
        super(player);
    }

    public void handlePackage(Package pkg) {
        if (!pkg.hasKey("player")) return;
        if (pkg.getInteger("player") != getPlayer().getID()) return;

        if (pkg.getType() == PackageTypes.PLAYER_SYNC_MOVEMENT_X) {
            moveX(pkg.getInteger("x"));
        } else if (pkg.getType() == PackageTypes.PLAYER_SYNC_SNEAK) {
            sneak(pkg.getBoolean("sneak"));
        } else if (pkg.getType() == PackageTypes.PLAYER_SYNC_JUMP) {
            jump();
        } else if (pkg.getType() == PackageTypes.PLAYER_SYNC_REACH) {
            reach(pkg.getFraction("x"), pkg.getFraction("y"));
        } else if (pkg.getType() == PackageTypes.PLAYER_SYNC_PUNCH) {
            punch(pkg.getFraction("x"), pkg.getFraction("y"));
        } else if (pkg.getType() == PackageTypes.PLAYER_SYNC_USE_ITEM) {
            useItem(pkg.getBoolean("use"));
        }


    }
}
