package site.alex_xu.dev.game.party_physics.game.engine.sound;


import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class SoundManager {
    private static SoundManager INSTANCE = null;
    private final HashMap<String, Sound> cache = new HashMap<>();

    private final SoundPlayerGroup playerGroupUI = new SoundPlayerGroup(32);
    public static SoundManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SoundManager();
        }
        return INSTANCE;
    }

    public Sound get(String path) {
        if (!cache.containsKey(path))
            try {
                InputStream stream = SoundManager.class.getClassLoader().getResourceAsStream(path);

                AudioInputStream ais = AudioSystem.getAudioInputStream(stream);

                // Get the AudioFormat of the AudioInputStream
                AudioFormat format = ais.getFormat();
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);


                byte[] data = new byte[stream.available()];
                stream.read(data, 0, data.length);
                ais.close();
                stream.close();

                cache.put(path, new Sound(format, info, data));

            } catch (IOException | UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            }
        return cache.get(path);
    }

    public void cleanup() {
        SoundPlayer.shouldClose = true;
        playerGroupUI.dispose();
    }

    public SoundPlayerGroup getUIPlayerGroup() {
        return playerGroupUI;
    }
}
