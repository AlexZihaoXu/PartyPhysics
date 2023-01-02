package site.alex_xu.dev.game.party_physics.game.engine.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SoundPlayer {
    Sound sound;
    Clip clip;
    static boolean audioSystemNotWorking = false;

    ByteArrayInputStream stream;
    FloatControl gainControl;
    float volume = 1;

    public SoundPlayer() {
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean audioSystemWorking() {
        return clip != null && !audioSystemNotWorking;
    }

    private void initControls() {
        gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        setVolume(volume);
    }

    public void setVolume(float volume) {
        volume = Math.min(1, Math.max(0, volume));
        this.volume = volume;
        gainControl.setValue(20f * (float) Math.log10(volume));
    }

    public float getVolume() {
        return volume;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
        if (audioSystemWorking()) {
            try {
                if (stream != null) {
                    stream.close();
                }
                stream = sound.createStream();
                clip.open(AudioSystem.getAudioInputStream(stream));
                initControls();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                audioSystemNotWorking = true;
                throw new RuntimeException(e);
            }
        }
    }

    public void play() {
        this.clip.setFramePosition(0);
        this.clip.stop();
        this.clip.start();
    }

    public void stop() {
        this.clip.stop();
    }

    public void dispose() {
        this.clip.close();
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
