package site.alex_xu.dev.game.party_physics.game.engine.networking;

public enum PackageTypes {
    HANDSHAKE, // The very first package that will be sent to share basic information such as player's name
    PING,
    PONG,

    CLIENT_JOIN,
    CLIENT_LEAVE,
    CLIENT_UPDATE_LATENCY,

    CONNECTION_LOST,

    PHYSICS_SYNC_GAME_PLAYER_CREATE,

    PHYSICS_SYNC_GAME_UPDATE_COUNT,
    PHYSICS_SYNC_GAME_OBJECT_TRANSFORM,
    PHYSICS_SYNC_GAME_OBJECT_CREATE,

    PHYSICS_SYNC_GAME_PLAYER_GRAB,

    GAME_PLAYER_MOVEMENT_X_SET,
    GAME_PLAYER_MOVEMENT_JUMP,

    GAME_PLAYER_MOVEMENT_SNEAK,
    GAME_PLAYER_REACH_DIRECTION_SET,


}
