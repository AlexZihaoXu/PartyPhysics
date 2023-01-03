package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import com.jogamp.openal.AL;
import org.dyn4j.geometry.Vector3;

public class Listener {
    private static Listener INSTANCE;
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

    private boolean isPlayable() {
        return true; // TODO
    }

    public void setPosition(double x, double y, double z) {
        pos.set(x, y, z);
        if (isPlayable()) {
            temp[0] = (float) x;
            temp[1] = (float) y;
            temp[2] = (float) z;
            al.alListenerfv(AL.AL_POSITION, temp, 0);
        }
    }
    public void setVelocity(double x, double y, double z) {
        pos.set(x, y, z);
        if (isPlayable()) {
            temp[0] = (float) x;
            temp[1] = (float) y;
            temp[2] = (float) z;
            al.alListenerfv(AL.AL_VELOCITY, temp, 0);
        }
    }
}
