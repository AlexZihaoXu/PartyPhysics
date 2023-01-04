package site.alex_xu.dev.game.party_physics.game.content.stages;

import site.alex_xu.dev.game.party_physics.game.content.stages.menu.MenuStage;
import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.Sound;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;
import site.alex_xu.dev.game.party_physics.game.graphics.Font;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

import java.awt.*;

public class LoadingStage extends Stage implements Runnable {
    private boolean finishedLoading = false;

    private String loadingStatus = "...";
    private double progress = 0;

    private double displayProgress = 0;

    Thread loadThread = new Thread(this);

    private double whiteCoverProgress = 0;

    @Override
    public void onLoad() {
        super.onLoad();
        loadThread.start();
    }

    @Override
    public void onOffload() {
        super.onOffload();
    }

    @Override
    public void onRender(Renderer renderer) {
        super.onRender(renderer);
        if (whiteCoverProgress < 1) {

            renderer.pushState();

            renderer.translate(getWidth() / 2, getHeight() / 2);

            renderer.setColor(Color.white);
            renderer.clear();

            renderer.setColor(Color.black);

            renderer.setFont(Font.get("fonts/bulkypix.ttf"));

            if (progress > displayProgress) {
                double diff = progress - displayProgress;
                diff = Math.min(diff, 0.5);
                displayProgress += diff * getDeltaTime() * 15;
            }

            renderer.setTextSize(32);

            String text = String.format("Loading  %3.1f%% ", displayProgress * 100);
            double width = renderer.getTextWidth(text);

            renderer.setColor(new Color(183, 183, 183));
            renderer.text(text, -width / 2 + 3, -renderer.getTextHeight() / 2 + 3);
            renderer.setColor(new Color(56, 56, 56));
            renderer.text(text, -width / 2, -renderer.getTextHeight() / 2);


            renderer.popState();

            renderer.pushState();

            renderer.translate(0, getHeight() * 0.75 - 20);

            renderer.setColor(new Color(183, 183, 183));
            renderer.rect(55, 5, getWidth() - 100, 30);

            renderer.setColor(new Color(56, 56, 56));
            renderer.rect(50, 0, getWidth() - 100, 30);

            renderer.setFont(Font.get("fonts/bulkypix.ttf"));

            renderer.translate(getWidth() / 2, 50);

            renderer.setTextSize(15);
            width = renderer.getTextWidth(loadingStatus);
            renderer.setColor(new Color(210, 210, 210));
            renderer.text(loadingStatus, -width / 2 + 3, -renderer.getTextHeight() / 2 + 3);
            renderer.setColor(new Color(110, 110, 110));
            renderer.text(loadingStatus, -width / 2, -renderer.getTextHeight() / 2);

            renderer.popState();
        }

        renderer.setColor(255, 255, 255, (int) Math.min(255, whiteCoverProgress * 255));
        renderer.clear();



        if (whiteCoverProgress >= 1){
            renderer.setColor(210, 195, 171, (int) Math.min(255, (whiteCoverProgress - 1) * 255));
            renderer.clear();
        }

        if (whiteCoverProgress >= 2) {
            getWindow().changeStage(new MenuStage());
        }

        if (finishedLoading && displayProgress > 0.8) {
            whiteCoverProgress += getDeltaTime() * 2;
        }
    }

    @Override
    public void run() {
        String[] soundsToLoad = {
                "sounds/bgm-0.wav",
                "sounds/bgm-1.wav",
                "sounds/ui/mouse-click-0.wav",
                "sounds/ui/mouse-over-0.wav",
        };

        for (int i = 0; i < soundsToLoad.length; i++) {
            loadingStatus = "Loading sound: " + soundsToLoad[i];
            Sound.preload(soundsToLoad[i]);
            progress = (double) i / soundsToLoad.length;
        }
        loadingStatus = "Finished!";

        progress = 1;

        finishedLoading = true;
    }
}
