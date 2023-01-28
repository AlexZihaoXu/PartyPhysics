package site.alex_xu.dev.game.party_physics.game.engine.networking;

/**
 * An enumerate class of all package types for server/client communication
 */
public enum PackageTypes {
    HANDSHAKE, // The very first package that will be sent to share basic information such as player's name
    PING,
    PONG,

    CLIENT_JOIN,
    CLIENT_LEAVE,
    CLIENT_UPDATE_LATENCY,

    WORLD_SYNC_CREATE,

    WORLD_SYNC_ADD_PLAYER,

    WORLD_SYNC_OBJECT_STATE,

    WORLD_SYNC_REMOVE_PLAYER,
    WORLD_SYNC_ADD_OBJECT,
    PHYSICS_SYNC_GAME_UPDATE_COUNT,

    PLAYER_SYNC_GRAB_ITEM,

    PLAYER_SYNC_MOVEMENT_X,
    PLAYER_SYNC_JUMP,

    PLAYER_SYNC_SNEAK,
    PLAYER_SYNC_REACH,
    PLAYER_SYNC_PUNCH, PLAYER_SYNC_USE_ITEM, CAMERA_ADD_SHAKE, PLAYER_SYNC_HEALTH_UPDATE, WORLD_SYNC_REMOVE_OBJECT, SOUND_PLAY,


}
