package site.alex_xu.dev.game.party_physics.game.engine.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class SoundManager {
    private static SoundManager INSTANCE = null;
    private final HashMap<String, Sound> cache = new HashMap<>();

    public static SoundManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SoundManager();
        }
        return INSTANCE;
    }

    public Sound get(String path) {
        if (!cache.containsKey(path))
            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
                assert inputStream != null;
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                cache.put(path, new Sound(bytes));
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        return cache.get(path);
    }

    public static void main(String[] args) {

    }
}
