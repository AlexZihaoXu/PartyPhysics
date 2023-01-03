package site.alex_xu.dev.game.party_physics.game.engine.sounds;


import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class SoundSystem {
    private static SoundSystem INSTANCE = null;

    public static SoundSystem getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SoundSystem();
        return INSTANCE;
    }

    //

    private boolean initialized = false;
    AL al;

    public void init() {
        if (initialized) return;

        al = ALFactory.getAL();
        ALut.alutInit();

        int[] buffer = new int[1];
        int[] source = new int[1];
        int[] format = new int[1];
        int[] size = new int[1];
        int[] freq = new int[1];
        int[] loop = new int[1];
        ByteBuffer[] data = new ByteBuffer[1];

        InputStream stream = getClass().getClassLoader().getResourceAsStream("sounds/bgm-0-original.wav");

        ALut.alutLoadWAVFile(stream, format, data, size, freq, loop);

        al.alGenSources(1, source, 0);
        al.alSourcei(source[0], AL.AL_BUFFER, buffer[0]);
        al.alSourcePlay(source[0]);

        al.alGetSourcei(source[0], AL.AL_SOURCE_STATE, source, 0);
        while (source[0] == AL.AL_PLAYING) {
            al.alGetSourcei(source[0], AL.AL_SOURCE_STATE, source, 0);
        }

        // Clean up
        al.alDeleteSources(1, source, 0);
        ALut.alutExit();


        initialized = true;
    }

}