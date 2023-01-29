package site.alex_xu.dev.game.party_physics.game.content;

/**
 * A class that stores settings of the games
 */
public class GameSettings {
    private static GameSettings INSTANCE = null;

    public static GameSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GameSettings();
        }
        return INSTANCE;
    }

    public double volumeMaster = 2/3d;
    public double volumeBackgroundMusic = 0.1;

    public double volumeUI = 1;

    public int antiAliasingLevel = 0;

}
