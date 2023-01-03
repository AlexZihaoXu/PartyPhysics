package site.alex_xu.dev.game.party_physics.game.content.test;

import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundManager;
import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundPlayer;
import site.alex_xu.dev.game.party_physics.game.engine.sound.SoundPlayerSyncer;

public class AudioPlayerTest {
    public static void main(String[] args) throws InterruptedException {
        SoundPlayer player = new SoundPlayer();
        SoundPlayer player2 = new SoundPlayer();
        player2.setSound(SoundManager.getInstance().get("sounds/bgm-0-muffled.wav"));
        player.setSound(SoundManager.getInstance().get("sounds/bgm-0-original.wav"));
        player.setVolume(0.1);
        player2.setVolume(0.1 * 0.8);
        player.play();
        player2.play();


        Thread.sleep(1000);
        player.setPosInBytes(1000);
        Thread.sleep(2000);

        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            SoundPlayerSyncer syncer = new SoundPlayerSyncer(player, player2);
            while (!player.isFinished()) {
                syncer.sync();
                int diff = Math.abs(player2.getPosInBytes() - player.getPosInBytes());
                System.out.println(i + " syncing: " + !syncer.succeed() + " diff: " + diff + " avg: " + (int) syncer.getAverageDiff());
                Thread.sleep(10, 500);
            }
            player.play();
            player2.play();
        }


        player.dispose();
        player2.dispose();
    }
}
