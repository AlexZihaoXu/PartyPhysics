package site.alex_xu.dev.game.party_physics.game.content.objects;

import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import site.alex_xu.dev.game.party_physics.game.content.player.Player;
import site.alex_xu.dev.game.party_physics.game.engine.framework.GameObject;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public abstract class GameObjectItem extends GameObject {

    private static final HashMap<String, Triangle[]> cache = new HashMap<>();
    private static final HashMap<String, Triangle[]> cacheFlipped = new HashMap<>();

    private boolean requestUse = false;

    private Player heldPlayer = null;

    private boolean isFlipped = false;

    private double physicsTime = 0;

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

    public void setHoldPlayer(Player player) {
        this.heldPlayer = player;
    }

    public boolean isHoldByPlayer() {
        return heldPlayer != null;
    }

    public void forceUpdateModel(boolean flipped) {
        updateModel(flipped);
        isFlipped = flipped;
        updateMass();
    }

    protected abstract void updateModel(boolean flipped);

    public void setFlipped(boolean isFlipped) {
        if (isFlipped != this.isFlipped) {
            updateModel(isFlipped);
            updateMass();
        }
        this.isFlipped = isFlipped;
    }

    public boolean isFlipped() {
        return isFlipped;
    }

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

    public static Triangle[] getModel(String path) {
        loadModel(path);
        return cache.get(path);
    }

    public static Triangle[] getFlippedModel(String path) {
        loadModel(path);
        return cacheFlipped.get(path);
    }

    protected abstract void onUse(Player user);

    public void use() {
        requestUse = true;
    }
}
