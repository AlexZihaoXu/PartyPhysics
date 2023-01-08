package site.alex_xu.dev.game.party_physics.game.engine.sounds;

import org.dyn4j.geometry.Vector3;

import java.util.HashSet;

public class SoundSource {

    static final HashSet<SoundSource> sources = new HashSet<>();
    BaseSoundSource sourceOriginal = new BaseSoundSource();
    BaseSoundSource sourceMuffled = new BaseSoundSource();

    private double muffleShift = 0;
    private double volume = 1;

    public SoundSource() {
        sources.add(this);
    }

    public void setSound(Sound sound) {
        sourceOriginal.setSound(sound);
        sourceMuffled.setSound(sound.getMuffled());
    }

    public void setSound(String path) {
        setSound(Sound.get(path));
    }

    public void setVolume(double volume) {
        this.volume = volume;
        updateVolume();
    }

    void updateVolume() {
        double muffle = Math.max(muffleShift, SoundSystem.getInstance().getMasterMuffle());
        sourceOriginal.setVolume(volume * (1 - muffle));
        sourceMuffled.setVolume(volume * muffle);
    }

    public void setPitch(double pitch) {
        sourceOriginal.setPitch(pitch);
        sourceMuffled.setPitch(pitch);
    }

    public void play() {
        sourceOriginal.play();
        sourceMuffled.play();
    }

    public void stop() {
        sourceOriginal.stop();
        sourceMuffled.stop();
    }

    public void sync() {
        if (Math.abs(sourceOriginal.getLength() - sourceMuffled.getLength()) > 0.01) {
            if (muffleShift > 0.5) {
                sourceMuffled.setSecondOffset(sourceOriginal.getSecondOffset());
            } else {
                sourceOriginal.setSecondOffset(sourceMuffled.getSecondOffset());
            }
        }
    }

    public void setMufflePercentage(double percentage) {
        muffleShift = percentage;
        updateVolume();
    }

    public void pause() {
        sourceOriginal.pause();
        sourceMuffled.pause();
    }

    public void delete() {
        sourceOriginal.delete();
        sourceMuffled.delete();
        sources.remove(this);
    }

    public double getLength() {
        return sourceOriginal.getLength();
    }

    public double getSecondOffset() {
        return sourceOriginal.getSecondOffset();
    }

    public double getProgress() {
        return sourceOriginal.getProgress();
    }

    public boolean isPlaying() {
        return sourceOriginal.isPlaying();
    }

    public boolean isStopped() {
        return sourceOriginal.isStopped();
    }

    public boolean isPaused() {
        return sourceOriginal.isPaused();
    }

    public void setPosition(double x, double y, double z) {
        sourceOriginal.setPosition(x, y, z);
        sourceMuffled.setPosition(x, y, z);
    }

    public void setVelocity(double x, double y, double z) {
        sourceOriginal.setVelocity(x, y, z);
        sourceMuffled.setVelocity(x, y, z);
    }

    public double getMufflePercentage() {
        return muffleShift;
    }
}
