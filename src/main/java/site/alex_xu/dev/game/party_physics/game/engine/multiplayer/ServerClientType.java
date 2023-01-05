package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;

public interface ServerClientType {

    boolean isCrashed();
    void shutdown();

    void flush();

    Package pull();
}
