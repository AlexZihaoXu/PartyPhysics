package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import com.jogamp.openal.AL;
import org.dyn4j.geometry.Vector3;

/**
 * The listener class that makes hearing possible
 */
public class Listener {
    /**
     * The only instance for listener class (Singleton)
     */
    private static Listener INSTANCE;

    /**
     * @return the instance of the listener class (creates one if it doesn't exist)
     */
    public static Listener getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Listener();
        }
        return INSTANCE;
    }

    private Vector3 pos = new Vector3();
    private Vector3 vel = new Vector3();

    private float[] temp = new float[3];
    private final AL al;

    public Listener() {
        SoundSystem.getInstance().init();
        al = SoundSystem.getInstance().al;
        setPosition(0, 0, 0);
        setVelocity(0, 0, 0);
        float[] orientation = {0, 0, -1, 0, 1, 0};
        al.alListenerfv(AL.AL_ORIENTATION, orientation, 0);
    }

    /**
     * @return true if the listener is working
     * (Doesn't work in cases like missing speakers)
     */
    private boolean isPlayable() {
        return true; // TODO
    }

    /**
     * @param x x-position
     * @param y y-position
     * @param z z-position
     */
    public void setPosition(double x, double y, double z) {
        pos.set(x, y, z);
        if (isPlayable()) {
            temp[0] = (float) x * SoundSystem.SCALE;
            temp[1] = (float) y * SoundSystem.SCALE;
            temp[2] = (float) z * SoundSystem.SCALE;
            al.alListenerfv(AL.AL_POSITION, temp, 0);
        }
    }

    /**
     * @param x x-velocity
     * @param y y-velocity
     * @param z z-velocity
     */
    public void setVelocity(double x, double y, double z) {
        pos.set(x, y, z);
        if (isPlayable()) {
            temp[0] = (float) x * SoundSystem.SCALE;
            temp[1] = (float) y * SoundSystem.SCALE;
            temp[2] = (float) z * SoundSystem.SCALE;
            al.alListenerfv(AL.AL_VELOCITY, temp, 0);
        }
    }
}
