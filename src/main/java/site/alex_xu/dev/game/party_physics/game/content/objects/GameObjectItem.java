package site.alex_xu.dev.game.party_physics.game.content.objects;

import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;

import java.awt.*;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * The parent class for all item objects
 */
public abstract class GameObjectItem extends GameObject {

    private static final HashMap<String, Triangle[]> cache = new HashMap<>();
    private static final HashMap<String, Triangle[]> cacheFlipped = new HashMap<>();

    private boolean requestUse = false;

    private Player heldPlayer = null;

    private boolean isFlipped = false;

    private double physicsTime = 0;

    @Override
    public Color getHitParticleColor() {
        return Color.darkGray;
    }

    public double getPhysicsTime() {
        return physicsTime;
    }

    @Override
    public void onPhysicsTick(double dt) {
        super.onPhysicsTick(dt);
        physicsTime += dt;
        if (requestUse) {
            onUse(heldPlayer);
            requestUse = false;
        }
    }

    /**
     * Set a holding player
     * @param player the holding player
     */
    public void setHoldPlayer(Player player) {
        this.heldPlayer = player;
    }

    /**
     * @return true if is currently holding by a player
     */
    public boolean isHoldByPlayer() {
        return heldPlayer != null;
    }

    /**
     * @param flipped true if the model should be flipped otherwise false
     */
    public void forceUpdateModel(boolean flipped) {
        updateModel(flipped);
        isFlipped = flipped;
        updateMass();
    }

    /**
     * @param flipped true if the model is flipped
     */
    protected abstract void updateModel(boolean flipped);

    /**
     * @param isFlipped true if the model is flipped otherwise false
     */
    public void setFlipped(boolean isFlipped) {
        if (isFlipped != this.isFlipped) {
            updateModel(isFlipped);
            updateMass();
        }
        this.isFlipped = isFlipped;
    }

    /**
     * @return true of the model is flipped otherwise false
     */
    public boolean isFlipped() {
        return isFlipped;
    }

    /**
     * Load a model from file (created in site.alex_xu.dev.game.party_physics.game.content.test.ModelEditingStage)
     * @param path the path to the model
     */
    private static void loadModel(String path) {
        if (!cache.containsKey(path)) {
            try {
                InputStream inputStream = GameObjectItem.class.getClassLoader().getResourceAsStream(path);
                if (inputStream == null) {
                    System.err.println("Could not found model from path: " + path);
                    throw new FileNotFoundException("Could not found model from path: " + path);
                }
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                int length = dataInputStream.readByte();
                Vector2[] vertices = new Vector2[length];
                for (int i = 0; i < length; i++) {
                    vertices[i] = new Vector2(
                            dataInputStream.readByte() / 30.0,
                            dataInputStream.readByte() / 30.0
                    );
                }

                {
                    Triangle[] triangles = new Triangle[length / 3];
                    for (int i = 0; i < triangles.length; i++) {
                        Vector2 v1 = vertices[i * 3];
                        Vector2 v2 = vertices[i * 3 + 1];
                        Vector2 v3 = vertices[i * 3 + 2];
                        try {
                            triangles[i] = new Triangle(v1, v2, v3);
                        } catch (IllegalArgumentException ignore) {
                            triangles[i] = new Triangle(v1, v3, v2);
                        }
                    }
                    cache.put(path, triangles);
                }
                {
                    Triangle[] triangles = new Triangle[length / 3];
                    for (int i = 0; i < triangles.length; i++) {
                        Vector2 v1 = new Vector2(vertices[i * 3]);
                        Vector2 v2 = new Vector2(vertices[i * 3 + 1]);
                        Vector2 v3 = new Vector2(vertices[i * 3 + 2]);
                        v1.y = -v1.y;
                        v2.y = -v2.y;
                        v3.y = -v3.y;
                        try {
                            triangles[i] = new Triangle(v1, v2, v3);
                        } catch (IllegalArgumentException ignore) {
                            triangles[i] = new Triangle(v1, v3, v2);
                        }
                    }
                    cacheFlipped.put(path, triangles);
                }


                dataInputStream.close();
                inputStream.close();

            } catch (IOException e) {
                System.err.println("Failed to load model from path: " + path);
                e.printStackTrace();
                throw new IllegalStateException("Failed to load model from path: " + path);
            }
        }
    }

    /**
     * @param path the path to the model
     * @return an array of triangles that forms the model
     */
    public static Triangle[] getModel(String path) {
        loadModel(path);
        return cache.get(path);
    }

    /**
     * @param path the path to the model
     * @return the flipped version of the model
     */
    public static Triangle[] getFlippedModel(String path) {
        loadModel(path);
        return cacheFlipped.get(path);
    }

    /**
     * @param user the user to use
     */
    protected abstract void onUse(Player user);

    /**
     * Try to use this item
     */
    public void use() {
        requestUse = true;
    }
}
