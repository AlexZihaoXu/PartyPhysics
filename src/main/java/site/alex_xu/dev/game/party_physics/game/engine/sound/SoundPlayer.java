package site.alex_xu.dev.game.party_physics.game.engine.sound;

import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SoundPlayer {
    private static class PlayThread implements Runnable {

        SoundPlayer player;
        boolean exit = false;

        ByteArrayInputStream stream;

        boolean finished = false;

        final Object lock = new Object();

        @Override
        public void run() {
            try {
                player.line.start();
                resetStream();
                SourceDataLine line = player.line;
                int numBytesRead = 0;
                byte[] buffer = new byte[line.getBufferSize()];
                while (noNeedToExit()) {
                    synchronized (lock) {
                        numBytesRead = stream.read(buffer, 0, buffer.length);
                    }
                    if (numBytesRead >= 0) {
                        finished = false;
                        try {
                            line.write(buffer, 0, numBytesRead);
                        } catch (IllegalArgumentException ignored) {
                            Thread.sleep(5);
                        }
                    } else {
                        finished = true;
                        while (finished && noNeedToExit()) {
                            Thread.sleep(5);
                        }
                    }
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
                finished = false;
            }
        }

        private boolean noNeedToExit() {
            return PartyPhysicsWindow.getInstance().isRunning() && !exit;
        }
    }

    Sound sound;

    Thread thread;
    PlayThread playThread;

    SourceDataLine line = null;

    FloatControl volumeControl;
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

    public void setVolume(double value) {
        if (volumeControl == null) {
            volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        }
        value = Math.min(1, Math.max(0, value));
        float v = Math.min(volumeControl.getMaximum(), Math.max(volumeControl.getMinimum(), 20.0f * (float) Math.log10(value)));
        volumeControl.setValue(v);
        this.volume = value;
    }

    public double getVolume() {
        return volume;
    }
}
