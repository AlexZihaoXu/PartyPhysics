package site.alex_xu.dev.game.party_physics.game.engine.networking;

public enum PackageTypes {
    HANDSHAKE,
    PING,

    CONNECTION_LOST,

    PHYSICS_SYNC_GAME_PLAYER_CREATE,

    PHYSICS_SYNC_GAME_UPDATE_COUNT,
    PHYSICS_SYNC_GAME_OBJECT_TRANSFORM,
    PHYSICS_SYNC_GAME_OBJECT_CREATE,

    GAME_PLAYER_MOVEMENT_X_SET,


}
