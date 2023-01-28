package site.alex_xu.dev.game.party_physics.game.engine.sounds;


import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;
import site.alex_xu.dev.game.party_physics.game.content.GameSettings;

import java.util.ArrayList;

/**
 * The base class for Sound System
 */
public class SoundSystem {
    /**
     * The only instance of this class (Singleton)
     */
    private static SoundSystem INSTANCE = null;

    /**
     * @return the instance (creates one if it doesn't exist)
     */
    public static SoundSystem getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SoundSystem();
        return INSTANCE;
    }


    //

    private boolean initialized = false;
    AL al;
    private double masterMuffle = 0;

    /**
     * @param masterVolume new master volume
     */
    public void setMasterVolume(double masterVolume) {
        GameSettings.getInstance().volumeMaster = masterVolume;
        for (BaseSoundSource source : BaseSoundSource.sources) {
            source.updateVolume();
        }
    }

    /**
     * @return the current muffle effect percentage
     */
    public double getMasterMuffle() {
        return masterMuffle;
    }

    /**
     * @param masterMuffle set muffle effect percentage
     */
    public void setMasterMuffle(double masterMuffle) {
        this.masterMuffle = masterMuffle;
        for (SoundSource source : SoundSource.sources) {
            source.updateVolume();
        }
    }

    /**
     * @return the master volume
     */
    public double getMasterVolume() {
        return GameSettings.getInstance().volumeMaster;
    }

    private SourceGroup sourceGroupUI;
    private SourceGroup sourceGroupGame, sourceGroupGame2, sourceGroupGame3;
    static final float SCALE = 0.125f;

    /**
     * @return the UI sound source group
     */
    public SourceGroup getUISourceGroup() {
        return sourceGroupUI;
    }

    /**
     * @return game sound source group #1
     */
    public SourceGroup getGameSourceGroup() {
        return sourceGroupGame;
    }

    /**
     * @return game sound source group #2
     */
    public SourceGroup getGameSourceGroup2() {
        return sourceGroupGame2;
    }

    /**
     * @return game sound source group #3
     */
    public SourceGroup getGameSourceGroup3() {
        return sourceGroupGame3;
    }

    /**
     * Initialize the sound system
     */
    public void init() {
        if (initialized) return;

        ALut.alutInit();
        al = ALFactory.getAL();

        sourceGroupUI = new SourceGroup(4);
        sourceGroupGame = new SourceGroup(32);
        sourceGroupGame2 = new SourceGroup(24);
        sourceGroupGame3 = new SourceGroup(16);

        initialized = true;

        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    /**
     * Finalize and clean up all the resources that was created
     */
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