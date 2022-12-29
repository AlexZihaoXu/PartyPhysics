package site.alex_xu.dev.game.party_physics.game.content.objects;

import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;

public abstract class GameObjectItem extends GameObject {
    public abstract void onUse(Player user);
}
