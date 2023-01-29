package site.alex_xu.dev.game.party_physics.game.engine.framework;

import java.io.InputStream;

/**
 * A class that handles all loading resources tasks
 */
public class ResourceManager {
    /**
     * @param path the path to the resource
     * @return input stream to the resource
     */
    public static InputStream get(String path) {
        return ResourceManager.class.getClassLoader().getResourceAsStream(path);
    }
}
