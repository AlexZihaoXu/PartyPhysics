package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

/**
 * Stage class
 * Made switching stages possible
 */
public class Stage {
    public Stage() {

    }

    /**
     * @return the window instnace
     */
    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    /**
     * @return the width of the window
     */
    public int getWidth() {
        return getWindow().getWidth();
    }

    /**
     * @return the height of the window
     */
    public int getHeight() {
        return getWindow().getHeight();
    }

    /**
     * @return the delta time between last tick and current tick
     */
    public double getDeltaTime() {
        return getWindow().getDeltaTime();
    }

    /**
     * @return current time in seconds
     */
    public double getCurrentTime() {
        return getWindow().getCurrentTime();
    }

    /**
     * @return mouse x-position
     */
    public double getMouseX() {
        return getWindow().getMouseX();
    }

    /**
     * @return mouse y-position
     */
    public double getMouseY() {
        return getWindow().getMouseY();
    }

    /**
     * @return mouse position
     */
    public Vector2 getMousePos() {
        return getWindow().getMousePos();
    }

    /**
     * @param button the button to check
     * @return true if the given button is pressed, otherwise false
     */
    public boolean getMouseButton(int button) {
        return getWindow().getMouseButton(button);
    }

    /**
     * @param keyCode the keycode to check
     * @return true if the key is pressed, otherwise false
     */
    public boolean isKeyPressed(int keyCode) {
        return getWindow().isKeyPressed(keyCode);
    }


    /**
     * Gets called when switched to this stage
     */
    public void onLoad() {

    }

    /**
     * Gets called when the window switched to another stage (when this stage is switched off) or the program exits
     */
    public void onOffload() {

    }

    /**
     * Gets called when the game requires to tick
     */
    public void onTick() {

    }

    /**
     * Gets called when the program requires to render
     * @param renderer the renderer to render
     */
    public void onRender(Renderer renderer) {
    }

    /**
     * Gets called when a key is pressed
     * @param keyCode the pressed key
     */
    public void onKeyPressed(int keyCode) {

    }

    /**
     * Gets called when a key is released
     * @param keyCode the released key
     */
    public void onKeyReleased(int keyCode) {

    }

    /**
     * Gets called when mouse is pressed
     * @param x x-position of the mouse
     * @param y y-position of the mouse
     * @param button pressed button
     */
    public void onMousePressed(double x, double y, int button) {

    }

    /**
     * Gets called when mouse is released
     * @param x x-position of the mouse
     * @param y y-position of the mouse
     * @param button pressed button
     */
    public void onMouseReleased(double x, double y, int button) {

    }

    /**
     * Gets called when mouse moved
     * @param x x-position of the mouse
     * @param y y-position of the mouse
     */
    public void onMouseMove(double x, double y) {

    }


}
