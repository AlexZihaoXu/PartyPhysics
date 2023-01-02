package site.alex_xu.dev.game.party_physics.game.engine.sound;

import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Sound {
    byte[] data;

    Sound(byte[] data) {
        this.data = data;
    }

    ByteArrayInputStream createStream() {
        return new ByteArrayInputStream(data);
    }

}
