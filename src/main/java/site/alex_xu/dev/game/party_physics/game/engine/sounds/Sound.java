package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import com.jogamp.openal.AL;
import com.jogamp.openal.util.ALut;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;

public class Sound {

    static HashSet<Sound> sounds = new HashSet<>();
    private boolean playable = true;
    private boolean deleted = false;
    int buffer, format, size, freq, loop;
    private final AL al;
    int[] ptrBuffer = new int[1];

    public boolean isPlayable() {
        return playable && !deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Sound(InputStream stream) {
        SoundSystem.getInstance().init();
        al = SoundSystem.getInstance().al;
        if (stream == null) {
            playable = false;
            return;
        }

        int[] ptrFormat = new int[1];
        int[] ptrSize = new int[1];
        ByteBuffer[] ptrData = new ByteBuffer[1];
        int[] ptrFreq = new int[1];
        int[] ptrLoop = new int[1];

        al.alGenBuffers(1, ptrBuffer, 0);
        int error = al.alGetError();
        if (error != AL.AL_NO_ERROR) {
            playable = false;
            System.err.println("Unable to generate AL buffer for Sound! [" + error + "]");
            return;
        }

        sounds.add(this);

        ALut.alutLoadWAVFile(stream, ptrFormat, ptrData, ptrSize, ptrFreq, ptrLoop);
        buffer = ptrBuffer[0];
        size = ptrSize[0];
        freq = ptrFreq[0];
        loop = ptrLoop[0];
        format = ptrFormat[0];

        al.alBufferData(buffer, format, ptrData[0], size, freq);

        error = al.alGetError();
        if (error != AL.AL_NO_ERROR) {
            playable = false;
            System.err.println("Error check failed after creating Sound! [" + error + "]");
        }
    }

    public void delete() {
        if (isPlayable()) {
            al.alDeleteBuffers(1, ptrBuffer, 0);
            sounds.remove(this);
            deleted = true;
        }
    }
}
