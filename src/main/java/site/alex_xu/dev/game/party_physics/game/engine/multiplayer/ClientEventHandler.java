package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

/**
 * Force networking classes to have client handling methods
 */
public interface ClientEventHandler {
    void onClientJoin(Client client);

    void onClientLeave(Client client);

}
