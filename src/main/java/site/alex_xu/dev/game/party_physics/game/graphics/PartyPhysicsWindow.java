package site.alex_xu.dev.game.party_physics.game.graphics;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.*;

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
        if (switchingStage == null)
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

    public void setAALevel(int level) {
        activeRenderingFrame.aaLevel = level;
    }


    public void setAutoSwitchAALevelEnabled(boolean enabled) {
        activeRenderingFrame.autoSwitchAALevel = enabled;
    }

    // Getters

    public boolean isKeyPressed(int keyCode) {
        if (0 <= keyCode && keyCode < 256)
            return this.activeRenderingFrame.keyStatus[keyCode];
        return false;
    }

    public boolean getMouseButton(int button) {
        return activeRenderingFrame.mouseButtons[button];
    }

    public int getWidth() {
        return activeRenderingFrame.width;
    }

    public int getHeight() {
        return activeRenderingFrame.height;
    }

    public double getMouseX() {
        return activeRenderingFrame.mouseX;
    }

    public double getMouseY() {
        return activeRenderingFrame.mouseY;
    }

    public Vector2 getMousePos() {
        return new Vector2(getMouseX(), getMouseY());
    }

    public double getDeltaTime() {
        return activeRenderingFrame.dt;
    }

    public double getCurrentTime() {
        return activeRenderingFrame.now;
    }

    public int getAALevel() {
        return activeRenderingFrame.aaLevel;
    }

    public Stage getStage() {
        return stage;
    }

    public double getVideoDt() {
        return activeRenderingFrame.videoDt;
    }
}
