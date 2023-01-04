package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import org.dyn4j.geometry.Vector3;

public class SourceGroup {
    SoundSource[] sources;
    private int index = 0;

    private double gain = 1;

    public SourceGroup(int capacity) {
        sources = new SoundSource[capacity];
    }

    public SourceGroup() {
        this(8);
    }

    public void play(Sound sound) {
        if (SoundSystem.getInstance().isEverythingMuffled())
            sound = sound.getMuffled();
        for (int i = 0; i < sources.length; i++) {
            if (sources[index] == null) {
                sources[index] = new SoundSource();
            }

            if (sources[index].isStopped()) {
                sources[index].setSound(sound);
                sources[index].setGain(gain);
                sources[index].play();
                return;
            }
            index++;
            if (index >= sources.length) {
                index = 0;
            }
        }
        sources[index].stop();
        sources[index].setSound(sound);
        sources[index].setGain(gain);
        sources[index].play();
    }

    public void play(String path) {
        play(Sound.get(path));
    }

    public void playMuffled(String path) {
        play(Sound.get(path));
    }

    public void setGain(double gain) {
        gain = Math.min(1, Math.max(0, gain));
        this.gain = gain;
        for (SoundSource source : sources) {
            if (source != null) {
                source.setGain(gain);
            }
        }
    }

    public void delete() {
        for (SoundSource source : sources) {
            if (source != null)
                source.delete();
        }
    }

    public double getGain() {
        return gain;
    }
}
