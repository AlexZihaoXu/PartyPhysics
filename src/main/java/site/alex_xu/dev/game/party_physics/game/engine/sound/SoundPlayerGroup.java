package site.alex_xu.dev.game.party_physics.game.engine.sound;

public class SoundPlayerGroup {
    SoundPlayer[] players;

    SoundPlayerGroup(int groupSize) {
        players = new SoundPlayer[groupSize];
    }

    public SoundPlayerGroup() {
        this(32);
    }

    double volume = 1;

    int lastIndex = 0;

    public void play(Sound sound) {
        int index = lastIndex;

        for (int i = 0; i < players.length; i++) {
            index++;
            if (index >= players.length)
                index = 0;
            if (players[index] == null) {
                players[index] = new SoundPlayer();
            }
            if (players[index].isFinished()) {
                players[index].setSound(sound);
                players[index].setVolume(volume);
                players[index].play();
                lastIndex = index;
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
