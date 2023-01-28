package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALConstants;
import org.dyn4j.geometry.Vector3;

import java.util.HashSet;

/**
 * The base sound source that plays a single Sound object
 */
class BaseSoundSource {

    /**
     * A cache of all sources that have not been cleaned up
     */
    static HashSet<BaseSoundSource> sources = new HashSet<>();

    private final int[] ptrInt = new int[1];
    private final float[] ptrFloat = new float[1];
    private boolean playable = true;
    private int source;

    private final Vector3 pos = new Vector3();
    private final Vector3 vel = new Vector3();

    private Sound sound = null;

    private boolean deleted = false;

    private final AL al;

    private final float[] ptrFloat3 = new float[3];

    int[] ptrSource = new int[1];
    private double volume = 1;


    /**
     * @return true if the source is playable (not playable when sound system isn't working)
     */
    public boolean isPlayable() {
        return playable && !deleted;
    }

    /**
     * @return true if the sound buffer is deleted
     */
    public boolean isDeleted() {
        return deleted;
    }

    public BaseSoundSource() {
        SoundSystem.getInstance().init();

        al = SoundSystem.getInstance().al;

        al.alGenSources(1, ptrSource, 0);
        int error = al.alGetError();
        if (error != AL.AL_NO_ERROR) {
            playable = false;
            System.err.println("Unable to create SoundSource!");
            return;
        }

        source = ptrSource[0];

        setPitch(1);
        setVolume(1);
        setPosition(0, 0, 0);
        setVelocity(0, 0, 0);
        al.alSourcei(source, AL.AL_LOOPING, AL.AL_FALSE);

        if (al.alGetError() != AL.AL_NO_ERROR) {
            playable = false;
            System.err.println("Error check failed after creating SoundSource!");
        }
        sources.add(this);
    }

    /**
     * @param sound the sound object to play
     */
    public void setSound(Sound sound) {
        this.sound = sound;
        if (isPlayable()) {
            if (sound != null)
                al.alSourcei(source, AL.AL_BUFFER, sound.buffer);
        }
    }

    /**
     * @param pitch pitch effect (0~2, default=1)
     */
    public void setPitch(double pitch) {
        if (isPlayable())
            al.alSourcef(source, AL.AL_PITCH, (float) pitch);
    }

    /**
     * @param volume the new volume
     */
    public void setVolume(double volume) {
        this.volume = volume;
        updateVolume();
    }

    /**
     * Update the actual volume
     */
    void updateVolume() {
        if (isPlayable()) {
            al.alSourcef(source, AL.AL_GAIN, (float) (volume * SoundSystem.getInstance().getMasterVolume()));
        }
    }

    /**
     * @param x x-location
     * @param y y-location
     * @param z z-location
     */
    public void setPosition(double x, double y, double z) {
        pos.set(x, y, z);
        if (isPlayable()) {
            ptrFloat3[0] = (float) x * SoundSystem.SCALE;
            ptrFloat3[1] = (float) y * SoundSystem.SCALE;
            ptrFloat3[2] = (float) z * SoundSystem.SCALE;
            al.alSourcefv(source, AL.AL_POSITION, ptrFloat3, 0);
        }
    }

    /**
     * @param x x-velocity
     * @param y y-velocity
     * @param z z-velocity
     */
    public void setVelocity(double x, double y, double z) {
        vel.set(x, y, z);
        if (isPlayable()) {
            ptrFloat3[0] = (float) x * SoundSystem.SCALE;
            ptrFloat3[1] = (float) y * SoundSystem.SCALE;
            ptrFloat3[2] = (float) z * SoundSystem.SCALE;
            al.alSourcefv(source, AL.AL_VELOCITY, ptrFloat3, 0);
        }
    }

    /**
     * Play the bound sound object
     */
    public void play() {
        if (isPlayable()) {
            if (!isPlaying())
                al.alSourcePlay(source);
        }
    }

    /**
     * Pause the current playing sound
     */
    public void pause() {
        if (isPlayable()) {
            if (!isPaused())
                al.alSourcePause(source);
        }
    }

    /**
     * Stop playing the sound
     */
    public void stop() {
        if (isPlayable()) {
            al.alSourceStop(source);
        }
    }

    /**
     * @return current state (playing/paused/stopped)
     */
    private int getState() {
        if (isPlayable()) {
            al.alGetSourcei(source, ALConstants.AL_SOURCE_STATE, ptrInt, 0);
            return ptrInt[0];
        }
        return -1;
    }

    /**
     * @return true if the source is playing
     */
    public boolean isPlaying() {
        return getState() == AL.AL_PLAYING;
    }

    /**
     * @return true if the source is stopped
     */
    public boolean isStopped() {
        return getState() == AL.AL_STOPPED;
    }

    /**
     * @return true if the source is paused
     */
    public boolean isPaused() {
        return getState() == AL.AL_PAUSED;
    }

    /**
     * @return the playing progress in seconds
     */
    public double getSecondOffset() {
        if (isPlayable()) {
            al.alGetSourcef(source, AL.AL_SEC_OFFSET, ptrFloat, 0);
            return ptrFloat[0];
        }
        return 0;
    }

    /**
     * @param offset the playing progress in seconds
     */
    public void setSecondOffset(double offset) {
        if (isPlayable()) {
            boolean shouldPlayAfter = isPlaying();
            pause();
            al.alSourcef(source, AL.AL_SEC_OFFSET, (float) offset);
            if (shouldPlayAfter)
                play();
        }
    }

    /**
     * @return the playing progress in percentage (0.0~1.0)
     */
    public double getProgress() {
        if (isPlayable()) {
            if (isPlaying()) {
                return getSecondOffset() / getLength();
            }
        }
        return 0;
    }

    /**
     * @return the sound length in seconds
     */
    public double getLength() {
        if (isPlayable()) {
            return sound.getLength();
        }
        return 0;
    }

    /**
     * Free up the memory space allocated for this sound source
     */
    public void delete() {
        if (isPlayable()) {
            al.alDeleteSources(1, ptrSource, 0);
            sources.remove(this);
            deleted = true;
        }
    }

    /**
     * @param path the path to the sound
     */
    public void setSound(String path) {
        setSound(Sound.get(path));
    }
}
