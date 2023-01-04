package site.alex_xu.dev.game.party_physics.game.engine.sounds;


import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;
import site.alex_xu.dev.game.party_physics.game.content.GameSettings;

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

    private double masterVolume = 1;

    private double masterMuffle = 0;

    public void setMasterVolume(double masterVolume) {
        GameSettings.getInstance().volumeMaster = masterVolume;
        for (BaseSoundSource source : BaseSoundSource.sources) {
            source.updateVolume();
        }
    }

    public double getMasterMuffle() {
        return masterMuffle;
    }

    public void setMasterMuffle(double masterMuffle) {
        this.masterMuffle = masterMuffle;
        for (SoundSource source : SoundSource.sources) {
            source.updateVolume();
        }
    }

    public double getMasterVolume() {
        return GameSettings.getInstance().volumeMaster;
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
            ArrayList<BaseSoundSource> sources = new ArrayList<>(BaseSoundSource.sources);
            for (BaseSoundSource source : sources) {
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