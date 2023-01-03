package site.alex_xu.dev.game.party_physics.game.engine.sound;

import site.alex_xu.dev.game.party_physics.game.utils.Clock;

import java.util.LinkedList;

public class SoundPlayerSyncer {
    public SoundPlayer synced, syncing;
    Clock timer = new Clock();
    boolean succeed = false;

    LinkedList<Integer> diffs = new LinkedList<>();
    int diffSum = 0;

    public SoundPlayerSyncer(SoundPlayer synced, SoundPlayer syncing) {
        this.synced = synced;
        this.syncing = syncing;
    }

    public double getAverageDiff() {
        return this.diffSum / (double) diffs.size();
    }

    public void sync() {
        if (timer.elapsedTime() > 0.01) {
            timer.reset();
            int diff = Math.abs(synced.getPosInBytes() - syncing.getPosInBytes());
            diffs.addLast(diff);
            diffSum += diff;
            while (diffs.size() > 50) {
                diffSum -= diffs.removeFirst();
            }

            int bufferSize = syncing.playThread.buffer.length;

            if (diff == 0 && getAverageDiff() < bufferSize) {
                succeed = true;
            }
            if (!succeed || getAverageDiff() >= bufferSize) {
                succeed = false;
                synced.setPosInBytes(syncing.getPosInBytes());
            }
        }
    }

    public boolean succeed() {
        return succeed;
    }
}
