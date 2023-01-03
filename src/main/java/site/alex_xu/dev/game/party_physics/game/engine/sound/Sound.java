package site.alex_xu.dev.game.party_physics.game.engine.sound;

import javax.sound.sampled.*;
public class Sound {
    AudioFormat format;
    DataLine.Info info;

    byte[] data;

    Sound(AudioFormat format, DataLine.Info info, byte[] data) {
        this.format = format;
        this.info = info;
        this.data = data;
    }

}
