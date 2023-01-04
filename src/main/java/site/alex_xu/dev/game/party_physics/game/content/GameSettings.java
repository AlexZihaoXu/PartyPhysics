package site.alex_xu.dev.game.party_physics.game.content;

public class GameSettings {
    private static GameSettings INSTANCE = null;

    public static GameSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new GameSettings();
        }
        return INSTANCE;
    }

    public double volumeMaster = 1;
    public double volumeBackgroundMusic = 1 / 3d;

    public double volumeUI = 2 / 3d;

    public int antiAliasingLevel = 0;

}