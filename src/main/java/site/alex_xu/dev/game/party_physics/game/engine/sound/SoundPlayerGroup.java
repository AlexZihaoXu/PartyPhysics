package site.alex_xu.dev.game.party_physics.game.engine.sound;

public class SoundPlayerGroup {
    SoundPlayer[] players;

    SoundPlayerGroup(int groupSize) {
        players = new SoundPlayer[groupSize];
    }

    public SoundPlayerGroup() {
        this(512);
    }

    double volume = 1;

    public void play(Sound sound) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = new SoundPlayer();
            }
            if (players[i].isFinished()) {
                players[i].setSound(sound);
                players[i].setVolume(volume);
                players[i].play();
                return;
            }
        }
    }

    public void setVolume(double volume) {
        this.volume = volume;
        for (SoundPlayer player : players) {
            if (player != null)
                player.setVolume(volume);
        }
    }

    public double getVolume() {
        return volume;
    }

    public void dispose() {
        for (SoundPlayer player : players) {
            if (player != null) {
                player.dispose();
            }
        }
    }


}
