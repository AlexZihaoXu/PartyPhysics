package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import com.jogamp.openal.AL;
import com.jogamp.openal.util.ALut;
import site.alex_xu.dev.game.party_physics.game.engine.framework.ResourceManager;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Sound that holds a piece of audio
 */
public class Sound {

    /**
     * A cache of sounds (will be cleaned up when the program exits)
     */
    private static final HashMap<String, Sound> cache = new HashMap<>();
    static HashSet<Sound> sounds = new HashSet<>();
    private boolean playable = true;
    private boolean deleted = false;

    private byte[] data;
    int buffer, format, size, freq, loop;
    private final AL al;

    private double length = 0;
    int[] ptrBuffer = new int[1];

    private boolean isMuffled = false;

    /**
     * @param path preload sound from path (should be called in preload stage)
     */
    public static void preload(String path) {
        if (!cache.containsKey(path)) {
            Sound sound = new Sound(ResourceManager.get(path));
            cache.put(path, sound);
            sound.getMuffled();
        }
    }

    /**
     * @return true if the audio is properly loaded and is playable
     */
    public boolean isPlayable() {
        return playable && !deleted;
    }

    /**
     * @return true if the allocated memory space is already freed
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param path path to the audio file
     * @return a sound object (automatically caches audios from duplicated paths to save memory space and improve performance)
     */
    public static Sound get(String path) {
        preload(path);
        return cache.get(path);
    }

    /**
     * @param stream the data stream that provides the audio data
     */
    Sound(InputStream stream) {
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

        // Load and convert the audio data from the stream
        ALut.alutLoadWAVFile(stream, ptrFormat, ptrData, ptrSize, ptrFreq, ptrLoop);
        size = ptrSize[0];
        freq = ptrFreq[0];
        loop = ptrLoop[0];
        format = ptrFormat[0];

        ptrData[0].rewind();
        data = new byte[ptrData[0].remaining()];
        ptrData[0].get(data);

        ByteBuffer newData = ByteBuffer.wrap(data);

        if (init(format, newData, size, freq)) { // Initialize with the converted audio data
            return;
        }
        sounds.add(this);
    }

    private Sound(int format, ByteBuffer data, int size, int freq) {
        SoundSystem.getInstance().init();
        al = SoundSystem.getInstance().al;
        if (init(format, data, size, freq)) {
            return;
        }
        sounds.add(this);
    }

    /**
     * @return a muffled copy of this sound
     */
    Sound getMuffled() {
        if (this.isMuffled)
            return this;

        byte[] data = Arrays.copyOf(this.data, this.data.length);

        applyMuffleEffect(data);
        ByteBuffer newData = ByteBuffer.wrap(data);

        Sound sound = new Sound(format, newData, size, freq);

        newData.clear();
        sound.isMuffled = true;
        return sound;
    }

    /**
     * @param format format
     * @param newData data
     * @param size size
     * @param freq frequency
     * @return true if error occurs during initialization
     */
    private boolean init(int format, ByteBuffer newData, int size, int freq) {
        al.alGenBuffers(1, ptrBuffer, 0);
        buffer = ptrBuffer[0];
        int error = al.alGetError();
        if (error != AL.AL_NO_ERROR) {
            playable = false;
            System.err.println("Unable to generate AL buffer for Sound! [" + error + "]");
            return true;
        }

        al.alBufferData(buffer, format, newData, size, freq);

        int[] ptrInt = new int[1];
        al.alGetBufferi(buffer, AL.AL_CHANNELS, ptrInt, 0);
        int channels = ptrInt[0];
        al.alGetBufferi(buffer, AL.AL_BITS, ptrInt, 0);
        int bits = ptrInt[0];

        length = (double) size / (freq * channels * (bits / 8d));

        error = al.alGetError();
        if (error != AL.AL_NO_ERROR) {
            playable = false;
            System.err.println("Error check failed after creating Sound! [" + error + "]");
            return true;
        }
        return false;
    }

    /**
     * @param data the audio data to be converted
     */
    public static void applyMuffleEffect(byte[] data) {
        // Convert the byte array to a short array
        short[] audioData = new short[data.length / 2];
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(audioData);

        int sampleRange = 75;

        int sum = 0;
        LinkedList<Integer> values = new LinkedList<>();

        for (int i = 0; i < audioData.length; i++) {
            int value = audioData[i];
            sum += value;
            values.addLast(value);

            while (values.size() > sampleRange) {
                sum -= values.removeFirst();
            }

            int avg = sum / values.size();
            audioData[i] = (short) Math.min(Short.MAX_VALUE, Math.max(Short.MIN_VALUE, avg));
        }

        // Convert the short array back to a byte array
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(audioData);
    }

    /**
     * @return length of the audio in seconds
     */
    public double getLength() {
        return length;
    }

    /**
     * Free up the memory space that was allocated for the sound buffer
     */
    public void delete() {
        if (isPlayable()) {
            al.alDeleteBuffers(1, ptrBuffer, 0);
            sounds.remove(this);
            deleted = true;
        }
    }

}
