package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import org.dyn4j.geometry.Vector3;

import java.util.HashSet;

/**
 * A Sound Source that plays sound
 */
public class SoundSource {

    /**
     * A cache of all sources that has not been cleaned up
     */
    static final HashSet<SoundSource> sources = new HashSet<>();
    BaseSoundSource sourceOriginal = new BaseSoundSource();
    BaseSoundSource sourceMuffled = new BaseSoundSource();

    private double muffleShift = 0;
    private double volume = 1;

    public SoundSource() {
        sources.add(this);
    }

    /**
     * @param sound set the sound to play
     */
    public void setSound(Sound sound) {
        sourceOriginal.setSound(sound);
        sourceMuffled.setSound(sound.getMuffled());
    }

    /**
     * @param path set the sound from given path
     */
    public void setSound(String path) {
        setSound(Sound.get(path));
    }

    /**
     * @param volume set volume
     */
    public void setVolume(double volume) {
        this.volume = volume;
        updateVolume();
    }

    /**
     * Calculate the volume muffled source and original source volume and update them
     */
    void updateVolume() {
        double muffle = Math.max(muffleShift, SoundSystem.getInstance().getMasterMuffle());
        sourceOriginal.setVolume(volume * (1 - muffle));
        sourceMuffled.setVolume(volume * muffle);
    }

    /**
     * @param pitch set the pitch
     */
    public void setPitch(double pitch) {
        sourceOriginal.setPitch(pitch);
        sourceMuffled.setPitch(pitch);
    }

    /**
     * Play the bound sound
     */
    public void play() {
        sourceOriginal.play();
        sourceMuffled.play();
    }

    /**
     * Stop playing
     */
    public void stop() {
        sourceOriginal.stop();
        sourceMuffled.stop();
    }

    /**
     * Sync the muffled sound and the original sound's playing progress
     */
    public void sync() {
        if (Math.abs(sourceOriginal.getLength() - sourceMuffled.getLength()) > 0.01) {
            if (muffleShift > 0.5) {
                sourceMuffled.setSecondOffset(sourceOriginal.getSecondOffset());
            } else {
                sourceOriginal.setSecondOffset(sourceMuffled.getSecondOffset());
            }
        }
    }

    /**
     * @param percentage the percentage of muffled effect
     */
    public void setMufflePercentage(double percentage) {
        muffleShift = percentage;
        updateVolume();
    }

    /**
     * Pause the current sound that is playing
     */
    public void pause() {
        sourceOriginal.pause();
        sourceMuffled.pause();
    }

    /**
     * Free up the memory space that was allocated for this sound source
     */
    public void delete() {
        sourceOriginal.delete();
        sourceMuffled.delete();
        sources.remove(this);
    }

    /**
     * @return the length in seconds of the sound
     */
    public double getLength() {
        return sourceOriginal.getLength();
    }

    /**
     * @return the current playing progress in seconds
     */
    public double getSecondOffset() {
        return sourceOriginal.getSecondOffset();
    }

    /**
     * @return the current playing progress in percentage (0.0~1.0)
     */
    public double getProgress() {
        return sourceOriginal.getProgress();
    }

    /**
     * @return true if the source is playing otherwise false
     */
    public boolean isPlaying() {
        return sourceOriginal.isPlaying();
    }

    /**
     * @return true if the source is stopped otherwise false
     */
    public boolean isStopped() {
        return sourceOriginal.isStopped();
    }

    /**
     * @return true if the source is paused otherwise false
     */
    public boolean isPaused() {
        return sourceOriginal.isPaused();
    }

    /**
     * Sets the 3-dimensional location of this sound source
     * @param x x-position
     * @param y y-position
     * @param z z-position
     */
    public void setPosition(double x, double y, double z) {
        sourceOriginal.setPosition(x, y, z);
        sourceMuffled.setPosition(x, y, z);
    }

    /**
     * @param x x-velocity
     * @param y y-velocity
     * @param z z-velocity
     */
    public void setVelocity(double x, double y, double z) {
        sourceOriginal.setVelocity(x, y, z);
        sourceMuffled.setVelocity(x, y, z);
    }

    /**
     * @return the percentage of the muffle effect
     */
    public double getMufflePercentage() {
        return muffleShift;
    }
}
