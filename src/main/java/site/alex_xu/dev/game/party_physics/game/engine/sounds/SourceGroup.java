package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import org.dyn4j.geometry.Vector3;

public class SourceGroup {
    SoundSource[] sources;
    private int index = 0;

    private final Vector3 location = new Vector3();
    private final Vector3 velocity = new Vector3();

    private double volume = 1;

    public SourceGroup(int capacity) {
        sources = new SoundSource[capacity];
    }

    public SourceGroup() {
        this(8);
    }

    public void setLocation(double x, double y, double z) {
        location.set(x, y, z);
    }

    public void setVelocity(double x, double y, double z) {
        velocity.set(x, y, z);
    }
    public void play(Sound sound) {
        for (int i = 0; i < sources.length; i++) {
            if (sources[index] == null) {
                sources[index] = new SoundSource();
            }

            if (sources[index].isStopped()) {
                sources[index].setSound(sound);
                sources[index].setPosition(location.x, location.y, location.z);
                sources[index].setVelocity(velocity.x, velocity.y, velocity.z);
                sources[index].setVolume(volume);
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
        sources[index].setVolume(volume);
        sources[index].setPosition(location.x, location.y, location.z);
        sources[index].setVelocity(velocity.x, velocity.y, velocity.z);
        sources[index].play();
    }

    public void play(String path) {
        play(Sound.get(path));
    }

    public void playMuffled(String path) {
        play(Sound.get(path));
    }

    public void setVolume(double volume) {
        volume = Math.min(1, Math.max(0, volume));
        this.volume = volume;
        for (SoundSource source : sources) {
            if (source != null) {
                source.setVolume(volume);
            }
        }
    }

    public void delete() {
        for (SoundSource source : sources) {
            if (source != null)
                source.delete();
        }
    }

    public double getVolume() {
        return volume;
    }
}
