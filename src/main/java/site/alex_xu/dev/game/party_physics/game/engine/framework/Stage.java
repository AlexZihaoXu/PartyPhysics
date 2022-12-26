package site.alex_xu.dev.game.party_physics.game.engine.framework;

import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.graphics.PartyPhysicsWindow;
import site.alex_xu.dev.game.party_physics.game.graphics.Renderer;

public class Stage {
    public Stage() {

    }

    public PartyPhysicsWindow getWindow() {
        return PartyPhysicsWindow.getInstance();
    }

    public int getWidth() {
        return getWindow().getWidth();
    }

    public int getHeight() {
        return getWindow().getHeight();
    }

    public double getDeltaTime() {
        return getWindow().getDeltaTime();
    }

    public double getCurrentTime() {
        return getWindow().getCurrentTime();
    }

    public double getMouseX() {
        return getWindow().getMouseX();
    }

    public double getMouseY() {
        return getWindow().getMouseY();
    }

    public Vector2 getMousePos() {
        return getWindow().getMousePos();
    }

    public boolean isKeyPressed(int keyCode) {
        return getWindow().isKeyPressed(keyCode);
    }


    public void onLoad() {

    }

    public void onOffload() {

    }

    public void onTick() {

    }

    public void onRender(Renderer renderer) {
    }

    public void onKeyPressed(int keyCode) {

    }

    public void onKeyReleased(int keyCode) {

    }

    public void onMousePressed(double x, double y, int button) {

    }

    public void onMouseReleased(double x, double y, int button) {

    }

    public void onMouseMove(double x, double y) {

    }


}
