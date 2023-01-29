package site.alex_xu.dev.game.party_physics.game.content.player;

import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;

import java.awt.*;

/**
 * The parent class for all player body parts
 */
public abstract class GameObjectPlayerPart extends GameObject {
    Player player;
    Color color = new Color(99, 194, 42, 255);

    public Player getPlayer() {
        return player;
    }

    @Override
    public Color getHitParticleColor() {
        return color;
    }
}
