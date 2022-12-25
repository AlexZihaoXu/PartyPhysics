package site.alex_xu.dev.game.party_physics.game.graphics;

import site.alex_xu.dev.game.party_physics.game.engine.framework.Stage;

public class PartyPhysicsWindow {
    private static PartyPhysicsWindow INSTANCE = null;
    final ActiveRenderingJFrame activeRenderingFrame;

    private Stage stage = new Stage();
    private Stage switchingStage = null;

    public static PartyPhysicsWindow getInstance() {
        if (INSTANCE == null) INSTANCE = new PartyPhysicsWindow();
        return INSTANCE;
    }

    private PartyPhysicsWindow() {
        activeRenderingFrame = new ActiveRenderingJFrame("Party Physics!", this);
    }

    public void start() {
        try {
            onSetup();
            activeRenderingFrame.mainLoop();
            activeRenderingFrame.running = false;
        } catch (IllegalStateException ignored) {
        } finally {
            onDestroy();
        }
    }

    public void onSetup() {
        stage.onLoad();
    }

    public void onDestroy() {
        stage.onOffload();
    }

    public void onRender(Renderer renderer) {
        stage.onRender(renderer);
    }

    public void onTick() {

        if (switchingStage != null && switchingStage != stage) {
            if (stage != null) {
                stage.onOffload();
            }
            stage = switchingStage;
            switchingStage = null;
            stage.onLoad();
        }

        stage.onTick();
    }

    public void changeStage(Stage newStage) {
        switchingStage = newStage;
    }

    // Setters

    public void setMSAALevel(int level) {
        activeRenderingFrame.msaaLevel = level;
    }

    // Getters

    public int getWidth() {
        return activeRenderingFrame.width;
    }

    public int getHeight() {
        return activeRenderingFrame.height;
    }

    public double getDeltaTime() {
        return activeRenderingFrame.dt;
    }

    public double getCurrentTime() {
        return activeRenderingFrame.now;
    }

    public int getMSAALevel() {
        return activeRenderingFrame.msaaLevel;
    }

}
