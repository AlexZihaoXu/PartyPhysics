package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import org.dyn4j.geometry.Vector3;

/**
 * A sound source group that helps play multiple sounds at the same time
 */
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
        this(4);
    }

    /**
     * @param x x-location
     * @param y y-location
     * @param z z-location
     */
    public void setLocation(double x, double y, double z) {
        location.set(x, y, z);
    }

    /**
     * @param x x-velocity
     * @param y y-velocity
     * @param z z-velocity
     */
    public void setVelocity(double x, double y, double z) {
        velocity.set(x, y, z);
    }

    /**
     * @param sound the sound object to play
     */
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

    /**
     * @param path the path of the sound to play
     */
    public void play(String path) {
        play(Sound.get(path));
    }

    /**
     * @param path play sound from given path with muffled effect
     */
    public void playMuffled(String path) {
        play(Sound.get(path));
    }

    /**
     * @param volume volume
     */
    public void setVolume(double volume) {
        volume = Math.min(1, Math.max(0, volume));
        this.volume = volume;
        for (SoundSource source : sources) {
            if (source != null) {
                source.setVolume(volume);
            }
        }
    }

    /**
     * Free up allocated memory space
     */
    public void delete() {
        for (SoundSource source : sources) {
            if (source != null)
                source.delete();
        }
    }

    /**
     * @return the volume of the sound group
     */
    public double getVolume() {
        return volume;
    }
}
