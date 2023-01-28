package site.alex_xu.dev.game.party_physics.game.engine.multiplayer;

import site.alex_xu.dev.game.party_physics.game.engine.networking.Package;

/**
 * Server Client Type
 * (helps to force all network class to have the same method names)
 */
public interface ServerClientType {

    boolean isCrashed();
    void shutdown();

    void flush();

    Package pull();
}
