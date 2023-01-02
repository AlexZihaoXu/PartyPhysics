package site.alex_xu.dev.game.party_physics.game.engine.sound;


import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public static void main(String[] args) {
        // The audio file to play
        String filePath = "sounds/bgm.wav";

        try {
            // Create an AudioInputStream to read the audio file
            InputStream stream = SoundManager.class.getClassLoader().getResourceAsStream("sounds/bgm-0-muffled.wav");

            AudioFormat format;
            {

                AudioInputStream ais = AudioSystem.getAudioInputStream(stream);

                // Get the AudioFormat of the AudioInputStream
                format = ais.getFormat();
            }

            // Open a SourceDataLine for playback
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);

            // Start the playback
            line.start();

            // Transfer the audio data from the AudioInputStream to the SourceDataLine
            int numBytesRead = 0;
            byte[] buffer = new byte[line.getBufferSize()];

            byte[] bytes = new byte[stream.available()];
            stream.read(bytes, 0, bytes.length);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            for (int i = 0; i < 2000; i++) {
                ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            }

            System.out.println(buffer.length);
            int count = 0;
            while (numBytesRead != -1) {
                numBytesRead = byteArrayInputStream.read(buffer, 0, buffer.length);
                if (numBytesRead >= 0) {
                    line.write(buffer, 0, numBytesRead);
                }
            }

            // Wait for the playback to complete
            line.drain();

            // Close the SourceDataLine
            line.close();
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }

}
