package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

public interface ClientEventHandler {
    void onClientJoin(Client client);

    void onClientLeave(Client client);

}
