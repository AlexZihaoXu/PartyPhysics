package site.alex_xu.dev.game.party_physics.game.graphics;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.engine.framework.*;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.HostingServer;
import site.alex_xu.dev.game.party_physics.game.engine.multiplayer.JoiningClient;
import site.alex_xu.dev.game.party_physics.game.engine.sounds.SoundSystem;

import javax.swing.*;

/**
 * The main window class for Party Physics Game
 */
public class PartyPhysicsWindow {
    /**
     * (Singleton) The only instance of PartyPhysicsWindow class
     */
    private static PartyPhysicsWindow INSTANCE = null;
    final ActiveRenderingJFrame activeRenderingFrame;

    private Stage stage = new Stage();
    private Stage switchingStage = null;


    /**
     * @return the instance (creates one if it doesn't exist)
     */
    public static PartyPhysicsWindow getInstance() {
        if (INSTANCE == null) INSTANCE = new PartyPhysicsWindow();
        return INSTANCE;
    }

    private PartyPhysicsWindow() {
        activeRenderingFrame = new ActiveRenderingJFrame("Party Physics!", this);
    }

    /**
     * @return the JFrame object behind
     */
    public JFrame getJFrame() {
        return activeRenderingFrame;
    }

    /**
     * Start the game loop
     */
    public void start() {
        try {
            SoundSystem.getInstance().init();
            onSetup();
            activeRenderingFrame.mainLoop();
            activeRenderingFrame.running = false;
        } catch (IllegalStateException ignored) {
        } finally {
            onDestroy();
            SoundSystem.getInstance().cleanup();
            HostingServer.cleanup();
            JoiningClient.cleanup();
        }
    }

    /**
     * Gets called when the game requires to setup/load
     */
    public void onSetup() {
        stage.onLoad();
    }

    /**
     * Gets called when the game is stopped/crashed
     */
    public void onDestroy() {
        stage.onOffload();
    }

    /**
     * @param renderer the renderer to display everything
     */
    public void onRender(Renderer renderer) {
        if (switchingStage == null)
            stage.onRender(renderer);
    }

    /**
     * Gets called when the game requires to tick
     */
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

    /**
     * @param newStage new stage to switch to
     */
    public void changeStage(Stage newStage) {
        switchingStage = newStage;
    }

    // Setters

    /**
     * @param level super sampling anti-aliasing level
     */
    public void setAALevel(int level) {
        activeRenderingFrame.aaLevel = level;
    }


    /**
     * @param enabled if auto switch AA level should be enabled
     */
    public void setAutoSwitchAALevelEnabled(boolean enabled) {
        activeRenderingFrame.autoSwitchAALevel = enabled;
    }

    // Getters

    /**
     * @param keyCode the keycode
     * @return true if the given key is pressed, otherwise false
     */
    public boolean isKeyPressed(int keyCode) {
        if (0 <= keyCode && keyCode < 256)
            return this.activeRenderingFrame.keyStatus[keyCode];
        return false;
    }

    /**
     * @param button the mouse button
     * @return true if the given mouse button is pressed, otherwise false
     */
    public boolean getMouseButton(int button) {
        return activeRenderingFrame.mouseButtons[button];
    }

    /**
     * @return the width of the window
     */
    public int getWidth() {
        return activeRenderingFrame.width;
    }

    /**
     * @return the height of the window
     */
    public int getHeight() {
        return activeRenderingFrame.height;
    }

    /**
     * @return the x-coordinate of mouse
     */
    public double getMouseX() {
        return activeRenderingFrame.mouseX;
    }

    /**
     * @return the y-coordinate of the mouse
     */
    public double getMouseY() {
        return activeRenderingFrame.mouseY;
    }

    /**
     * @return the position of the mouse
     */
    public Vector2 getMousePos() {
        return new Vector2(getMouseX(), getMouseY());
    }

    /**
     * @return the delta time in seconds between the current frame and the last frame
     */
    public double getDeltaTime() {
        return activeRenderingFrame.dt;
    }

    /**
     * @return current time in seconds
     */
    public double getCurrentTime() {
        return activeRenderingFrame.now;
    }

    /**
     * @return anti-aliasing level
     */
    public int getAALevel() {
        return activeRenderingFrame.aaLevel;
    }

    /**
     * @return the current stage object
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @return the render delta time in seconds
     */
    public double getVideoDt() {
        return activeRenderingFrame.videoDt;
    }

    /**
     * @return true if the game is running
     */
    public boolean isRunning() {
        return activeRenderingFrame.running;
    }

    /**
     * Close the window
     */
    public void close() {
        this.getJFrame().dispose();
        this.activeRenderingFrame.running = false;
    }
}
