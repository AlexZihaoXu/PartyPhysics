package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALConstants;
import org.dyn4j.geometry.Vector3;

import java.nio.IntBuffer;
import java.util.HashSet;

public class SoundSource {

    static HashSet<SoundSource> sources = new HashSet<>();

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


    public boolean isPlayable() {
        return playable && !deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    SoundSource() {
        SoundSystem.getInstance().init();

        al = SoundSystem.getInstance().al;

        al.alGenSources(1, ptrSource, 0);
        int error = al.alGetError();
        if (error != AL.AL_NO_ERROR) {
            System.out.println(error);
            playable = false;
            System.err.println("Unable to create SoundSource!");
            return;
        }

        source = ptrSource[0];

        setPitch(1);
        setGain(1);
        setPosition(0, 0, 0);
        setVelocity(0, 0, 0);
        al.alSourcei(source, AL.AL_LOOPING, AL.AL_FALSE);

        if (al.alGetError() != AL.AL_NO_ERROR) {
            playable = false;
            System.err.println("Error check failed after creating SoundSource!");
        }
        sources.add(this);
    }

    public void setSound(Sound sound) {
        this.sound = sound;
        if (isPlayable()) {
            if (sound != null)
                al.alSourcei(source, AL.AL_BUFFER, sound.buffer);
        }
    }

    public void setPitch(double pitch) {
        if (isPlayable())
            al.alSourcef(source, AL.AL_PITCH, (float) pitch);
    }

    public void setGain(double gain) {
        if (isPlayable())
            al.alSourcef(source, AL.AL_GAIN, (float) gain);
    }

    public void setPosition(double x, double y, double z) {
        pos.set(x, y, z);
        if (isPlayable()) {
            ptrFloat3[0] = (float) x;
            ptrFloat3[1] = (float) y;
            ptrFloat3[2] = (float) z;
            al.alSourcefv(source, AL.AL_POSITION, ptrFloat3, 0);
        }
    }

    public void setVelocity(double x, double y, double z) {
        vel.set(x, y, z);
        if (isPlayable()) {
            ptrFloat3[0] = (float) x;
            ptrFloat3[1] = (float) y;
            ptrFloat3[2] = (float) z;
            al.alSourcefv(source, AL.AL_VELOCITY, ptrFloat3, 0);
        }
    }

    public void play() {
        if (isPlayable()) {
            if (!isPlaying())
                al.alSourcePlay(source);
        }
    }

    public void pause() {
        if (isPlayable()) {
            if (!isPaused())
                al.alSourcePause(source);
        }
    }

    public void stop() {
        if (isPlayable()) {
            al.alSourceStop(source);
        }
    }

    private int getState() {
        if (isPlayable()) {
            al.alGetSourcei(source, ALConstants.AL_SOURCE_STATE, ptrInt, 0);
            return ptrInt[0];
        }
        return -1;
    }

    public boolean isPlaying() {
        return getState() == AL.AL_PLAYING;
    }

    public boolean isStopped() {
        return getState() == AL.AL_STOPPED;
    }

    public boolean isPaused() {
        return getState() == AL.AL_PAUSED;
    }

    public double getSecondOffset() {
        if (isPlayable()) {
            al.alGetSourcef(source, AL.AL_SEC_OFFSET, ptrFloat, 0);
            return ptrFloat[0];
        }
        return 0;
    }

    public void setSecondOffset(double offset) {
        if (isPlayable()) {
            boolean shouldPlayAfter = isPlaying();
            pause();
            al.alSourcef(source, AL.AL_SEC_OFFSET, (float) offset);
            if (shouldPlayAfter)
                play();
        }
    }

    public double getProgress() {
        if (isPlayable()) {
            if (isPlaying()) {
                return getSecondOffset() / getLength();
            }
        }
        return 0;
    }

    public double getLength() {
        if (isPlayable()) {
            return sound.getLength();
        }
        return 0;
    }

    public void delete() {
        if (isPlayable()) {
            al.alDeleteSources(1, ptrSource, 0);
            sources.remove(this);
            deleted = true;
        }
    }

}
