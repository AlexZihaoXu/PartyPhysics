package site.alex_xu.dev.game.party_physics.game.engine.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SoundPlayer {
    static boolean shouldClose = false;

    int posBytes = 0;

    public int getPosInBytes() {
        return posBytes;
    }

    static class PlayThread implements Runnable {

        SoundPlayer player;
        boolean exit = false;

        ByteArrayInputStream stream;

        boolean finished = false;

        final Object lock = new Object();

        int length = 0;
        byte[] buffer = new byte[16000];

        @Override
        public void run() {
            try {
                player.line.start();
                resetStream();
                SourceDataLine line = player.line;
                int numBytesRead;
                while (noNeedToExit()) {

                    long now = System.currentTimeMillis();
                    long intervalSize = 50;
                    long waitUntilTime = (now / intervalSize + 1) * intervalSize;
                    Thread.sleep(waitUntilTime - now);
                    synchronized (lock) {
                        numBytesRead = stream.read(buffer, 0, buffer.length);
                    }
                    if (numBytesRead >= 0) {
                        finished = false;
                        try {
                            line.write(buffer, 0, numBytesRead);
                        } catch (IllegalArgumentException ignored) {
                            Thread.sleep(5 - System.currentTimeMillis() % 5);
                        }
                    } else {
                        finished = true;
                        while (finished && noNeedToExit()) {
                            Thread.sleep(5 - System.currentTimeMillis() % 5);
                        }
                    }

                    player.progress = Math.max(0, Math.min(1, 1 - (stream.available() / (double) length)));
                    player.posBytes = (int) (length - stream.available());
                }

                try {
                    player.line.drain();
                    player.line.close();
                    stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                player.line.close();
            }

        }

        void resetStream() {
            synchronized (lock) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                stream = new ByteArrayInputStream(player.sound.data);
                length = stream.available();
                finished = false;
            }
        }


        private boolean noNeedToExit() {
            return !shouldClose && !exit;
        }
    }

    Sound sound;

    Thread thread;
    PlayThread playThread;

    SourceDataLine line = null;

    FloatControl volumeControl;

    double progress = 0;

    double volume = 1;

    public SoundPlayer() {
    }

    public boolean isFinished() {
        if (playThread != null) {
            return playThread.finished;
        }
        return true;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
        if (line == null) {
            try {
                line = (SourceDataLine) AudioSystem.getLine(sound.info);
                line.open(sound.format);
                setVolume(volume);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void play() {
        if (playThread == null) {
            playThread = new PlayThread();
            playThread.player = this;
            thread = new Thread(playThread);
            thread.start();
        }
        playThread.resetStream();
    }

    public void ready() {
        if (playThread != null) {
            playThread.exit = true;
        }
        playThread = new PlayThread();
        playThread.player = this;
        thread = new Thread(playThread);
        thread.start();
    }

    public void dispose() {
        if (playThread != null) {
            playThread.exit = true;
        }
    }

    public double getProgress() {
        return progress;
    }

    public void setPosInBytes(int posBytes) {
        synchronized (this.playThread.lock) {
            if (this.playThread.stream != null) {
                try {
                    playThread.stream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            playThread.stream = new ByteArrayInputStream(sound.data, posBytes, sound.data.length - posBytes);
            playThread.length = sound.data.length;
            playThread.finished = false;
        }
    }

    public void setVolume(double value) {
        try {
            if (volumeControl == null) {
                volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            }
            value = Math.min(1, Math.max(0, value));
            float v = Math.min(volumeControl.getMaximum(), Math.max(volumeControl.getMinimum(), 20.0f * (float) Math.log10(value)));
            volumeControl.setValue(v);
        } catch (NullPointerException ignored) {
        }
        this.volume = value;
    }

    public double getVolume() {
        return volume;
    }
}
