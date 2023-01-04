package site.alex_xu.dev.game.party_physics.game.engine.sounds;


import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

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
    private boolean muffleEverything = false;

    public void setMuffleEverything(boolean muffleEverything) {
        this.muffleEverything = muffleEverything;
    }

    public boolean isEverythingMuffled() {
        return muffleEverything;
    }

    private SourceGroup sourceGroupUI;

    public SourceGroup getUISourceGroup() {
        return sourceGroupUI;
    }

    public void init() {
        if (initialized) return;

        ALut.alutInit();
        al = ALFactory.getAL();

        sourceGroupUI = new SourceGroup();

        initialized = true;

        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    public void cleanup() {
        if (initialized) {
            ArrayList<SoundSource> sources = new ArrayList<>(SoundSource.sources);
            for (SoundSource source : sources) {
                source.delete();
            }

            ArrayList<Sound> sounds = new ArrayList<>(Sound.sounds);
            for (Sound sound : sounds) {
                sound.delete();
            }

            ALut.alutExit();

            initialized = false;
        }
    }

}